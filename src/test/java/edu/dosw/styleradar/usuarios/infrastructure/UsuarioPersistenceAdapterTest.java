package edu.dosw.styleradar.usuarios.infrastructure;

import edu.dosw.styleradar.usuarios.domain.exception.EmailYaRegistradoException;
import edu.dosw.styleradar.usuarios.domain.model.EstadoCuenta;
import edu.dosw.styleradar.usuarios.domain.model.Rol;
import edu.dosw.styleradar.usuarios.domain.model.Usuario;
import edu.dosw.styleradar.usuarios.infrastructure.adapter.out.persistence.UsuarioPersistenceAdapter;
import edu.dosw.styleradar.usuarios.infrastructure.adapter.out.persistence.repository.UsuarioJpaRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = {
        "INTERNAL_AUTH_USERNAME=usuario-de-prueba",
        "INTERNAL_AUTH_PASSWORD=password-de-prueba"
})
@ActiveProfiles("integration")
class UsuarioPersistenceAdapterTest {

    @Autowired
    private UsuarioPersistenceAdapter usuarioPersistenceAdapter;

    @Autowired
    private UsuarioJpaRepository usuarioJpaRepository;

    private BCryptPasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
        usuarioJpaRepository.deleteAll();
    }

    @Test
    void debeGuardarUsuarioEnPostgreSQL() {

        String password = "StyleRadar2026*";
        String passwordHash = passwordEncoder.encode(password);

        Usuario usuario = crearUsuario(
                "Laura Gómez",
                "laura@gmail.com",
                passwordHash
        );

        Usuario guardado = usuarioPersistenceAdapter.guardar(usuario);

        assertNotNull(guardado.getId());
        assertEquals("Laura Gómez", guardado.getNombre());
        assertEquals("laura@gmail.com", guardado.getEmail());
        assertEquals(Boolean.TRUE, guardado.getAceptaHabeasData());

        assertNotEquals(password, guardado.getPasswordHash());

        assertTrue(passwordEncoder.matches(
                password,
                guardado.getPasswordHash()
        ));

        assertTrue(
                usuarioPersistenceAdapter.buscarPorEmail("laura@gmail.com").isPresent()
        );
    }

    @Test
    void noDebeGuardarDosUsuariosConElMismoCorreo() {

        Usuario primero = crearUsuario(
                "Laura Gómez",
                "laura@gmail.com",
                passwordEncoder.encode("Password123")
        );

        usuarioPersistenceAdapter.guardar(primero);

        Usuario segundo = crearUsuario(
                "Laura Pérez",
                "laura@gmail.com",
                passwordEncoder.encode("Password456")
        );

        assertThrows(
                EmailYaRegistradoException.class,
                () -> usuarioPersistenceAdapter.guardar(segundo)
        );

        assertEquals(1, usuarioJpaRepository.count());
    }

    private Usuario crearUsuario(
            String nombre,
            String email,
            String passwordHash) {

        return new Usuario(
                null,
                nombre,
                email,
                "+57 300 123 4567",
                passwordHash,
                true,
                Rol.COMPRADOR,
                EstadoCuenta.ACTIVA,
                Instant.now()
        );
    }
}


