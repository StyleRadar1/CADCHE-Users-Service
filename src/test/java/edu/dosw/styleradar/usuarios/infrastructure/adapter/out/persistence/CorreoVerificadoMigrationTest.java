package edu.dosw.styleradar.usuarios.infrastructure.adapter.out.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationVersion;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers(disabledWithoutDocker = true)
class CorreoVerificadoMigrationTest {

    @Container
    private static final PostgreSQLContainer<?> POSTGRES =
            new PostgreSQLContainer<>("postgres:16");

    @Test
    void marcaCuentasExistentesComoVerificadasYDejaFalsasLasNuevas() throws Exception {
        Flyway.configure()
                .dataSource(POSTGRES.getJdbcUrl(), POSTGRES.getUsername(), POSTGRES.getPassword())
                .locations("classpath:db/migration")
                .target(MigrationVersion.fromVersion("5"))
                .load()
                .migrate();

        try (Connection connection = connection();
                Statement statement = connection.createStatement()) {
            statement.executeUpdate("""
                    INSERT INTO usuarios (nombre, email, password_hash, acepta_habeas_data,
                                          rol, estado, fecha_registro)
                    VALUES ('Cuenta anterior', 'anterior@example.com', 'hash', TRUE,
                            'COMPRADOR', 'ACTIVA', NOW())
                    """);
        }

        Flyway.configure()
                .dataSource(POSTGRES.getJdbcUrl(), POSTGRES.getUsername(), POSTGRES.getPassword())
                .locations("classpath:db/migration")
                .load()
                .migrate();

        assertCorreoVerificado("anterior@example.com", true);
        assertIdentidadExternaNula("anterior@example.com");

        try (Connection connection = connection();
                Statement statement = connection.createStatement()) {
            statement.executeUpdate("""
                    INSERT INTO usuarios (nombre, email, password_hash, acepta_habeas_data,
                                          rol, estado, fecha_registro)
                    VALUES ('Cuenta nueva', 'nueva@example.com', 'hash', TRUE,
                            'COMPRADOR', 'ACTIVA', NOW())
                    """);
        }

        assertCorreoVerificado("nueva@example.com", false);

        try (Connection connection = connection();
                PreparedStatement statement = connection.prepareStatement("""
                        INSERT INTO usuarios (nombre, email, password_hash, acepta_habeas_data,
                                              rol, estado, fecha_registro, provider, provider_id)
                        VALUES ('Cuenta Google', 'google@example.com', 'hash', FALSE,
                                'COMPRADOR', 'ACTIVA', NOW(), 'GOOGLE', 'google-sub-1')
                        """)) {
            assertEquals(1, statement.executeUpdate());
        }

        try (Connection connection = connection();
                PreparedStatement statement = connection.prepareStatement("""
                        INSERT INTO usuarios (nombre, email, password_hash, acepta_habeas_data,
                                              rol, estado, fecha_registro, provider, provider_id)
                        VALUES ('Duplicado Google', 'google-duplicado@example.com', 'hash', FALSE,
                                'COMPRADOR', 'ACTIVA', NOW(), 'GOOGLE', 'google-sub-1')
                        """)) {
            org.junit.jupiter.api.Assertions.assertThrows(
                    java.sql.SQLException.class, statement::executeUpdate);
        }
    }

    private Connection connection() throws Exception {
        return DriverManager.getConnection(
                POSTGRES.getJdbcUrl(), POSTGRES.getUsername(), POSTGRES.getPassword());
    }

    private void assertCorreoVerificado(String email, boolean expected) throws Exception {
        try (Connection connection = connection();
                PreparedStatement statement = connection.prepareStatement(
                        "SELECT correo_verificado FROM usuarios WHERE email = ?")) {
            statement.setString(1, email);
            try (ResultSet result = statement.executeQuery()) {
                assertTrue(result.next());
                assertEquals(expected, result.getBoolean("correo_verificado"));
            }
        }
    }

    private void assertIdentidadExternaNula(String email) throws Exception {
        try (Connection connection = connection();
                PreparedStatement statement = connection.prepareStatement(
                        "SELECT provider, provider_id FROM usuarios WHERE email = ?")) {
            statement.setString(1, email);
            try (ResultSet result = statement.executeQuery()) {
                assertTrue(result.next());
                assertNull(result.getString("provider"));
                assertNull(result.getString("provider_id"));
            }
        }
    }
}
