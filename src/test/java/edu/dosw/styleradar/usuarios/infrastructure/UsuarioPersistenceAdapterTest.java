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
                passwordHash,
                false
        );

        Usuario guardado = usuarioPersistenceAdapter.guardar(usuario);

        assertNotNull(guardado.getId());
        assertEquals("Laura Gómez", guardado.getNombre());
        assertEquals("laura@gmail.com", guardado.getEmail());
        assertEquals(Boolean.TRUE, guardado.getAceptaHabeasData());
        assertEquals(Boolean.FALSE, guardado.getCorreoVerificado());
        assertNotEquals(password, guardado.getPasswordHash());

        assertTrue(passwordEncoder.matches(
                password,
                guardado.getPasswordHash()
        ));

        Usuario recuperado = usuarioPersistenceAdapter
                .buscarPorEmail("laura@gmail.com")
                .orElseThrow();

        assertEquals(Boolean.FALSE, recuperado.getCorreoVerificado());
        assertEquals(175.0, recuperado.getHeightCm());
        assertEquals(96.0, recuperado.getChestCm());
        assertEquals(82.0, recuperado.getWaistCm());
        assertEquals(98.0, recuperado.getHipCm());
    }

    @Test
    void debeGuardarUsuarioConCorreoVerificadoEnPostgreSQL() {

        Usuario usuario = crearUsuario(
                "Julian Giral",
                "julian@gmail.com",
                passwordEncoder.encode("Password123"),
                true
        );

        Usuario guardado = usuarioPersistenceAdapter.guardar(usuario);

        assertNotNull(guardado.getId());
        assertEquals(Boolean.TRUE, guardado.getCorreoVerificado());

        Usuario recuperado = usuarioPersistenceAdapter
                .buscarPorEmail("julian@gmail.com")
                .orElseThrow();

        assertEquals(Boolean.TRUE, recuperado.getCorreoVerificado());
    }

    @Test
    void debeGuardarUsuarioSinMedidasCorporalesEnPostgreSQL() {

        Usuario usuario = crearUsuario(
                "Laura Pérez",
                "laura.perez@gmail.com",
                passwordEncoder.encode("Password123"),
                false,
                null,
                null,
                null,
                null,
                null
        );

        Usuario guardado = usuarioPersistenceAdapter.guardar(usuario);

        assertNotNull(guardado.getId());

        Usuario recuperado = usuarioPersistenceAdapter
                .buscarPorEmail("laura.perez@gmail.com")
                .orElseThrow();

        assertNull(recuperado.getHeightCm());
        assertNull(recuperado.getChestCm());
        assertNull(recuperado.getWaistCm());
        assertNull(recuperado.getHipCm());
    }

    @Test
    void noDebeGuardarDosUsuariosConElMismoCorreo() {

        Usuario primero = crearUsuario(
                "Laura Gómez",
                "laura@gmail.com",
                passwordEncoder.encode("Password123"),
                false
        );

        usuarioPersistenceAdapter.guardar(primero);

        Usuario segundo = crearUsuario(
                "Laura Pérez",
                "laura@gmail.com",
                passwordEncoder.encode("Password456"),
                false
        );

        assertThrows(
                EmailYaRegistradoException.class,
                () -> usuarioPersistenceAdapter.guardar(segundo)
        );

        assertEquals(1, usuarioJpaRepository.count());
    }

    @Test
    void debeFiltrarCuentasPorEstadoRolTextoYAplicarPaginacion() {

        usuarioPersistenceAdapter.guardar(crearUsuario(
                "Laura Gómez",
                "laura.gomez@example.com",
                passwordEncoder.encode("Password123"),
                false,
                "Chapinero"
        ));

        usuarioPersistenceAdapter.guardar(crearUsuario(
                "Laura Pérez",
                "laura.perez@example.com",
                passwordEncoder.encode("Password456"),
                false,
                "Usaquén"
        ));

        Usuario administrador = new Usuario(
                null,
                "Laura Admin",
                "laura.admin@example.com",
                null,
                passwordEncoder.encode("Password789"),
                true,
                Rol.ADMIN_STYLERADAR,
                EstadoCuenta.ACTIVA,
                Instant.now(),
                "Centro"
        );

        usuarioPersistenceAdapter.guardar(administrador);

        var filtradas = usuarioPersistenceAdapter.buscarCuentas(
                EstadoCuenta.ACTIVA, Rol.COMPRADOR, "GÓMEZ", 0, 10
        );

        assertEquals(1, filtradas.size());
        assertEquals("laura.gomez@example.com", filtradas.get(0).getEmail());
        assertEquals("Chapinero", filtradas.get(0).getBarrio());

        assertEquals(1L, usuarioPersistenceAdapter.contarCuentas(
                EstadoCuenta.ACTIVA, Rol.COMPRADOR, "GÓMEZ"
        ));

        var primeraPagina = usuarioPersistenceAdapter.buscarCuentas(
                null, null, "laura", 0, 1
        );

        var segundaPagina = usuarioPersistenceAdapter.buscarCuentas(
                null, null, "laura", 1, 1
        );

        assertEquals(1, primeraPagina.size());
        assertEquals(1, segundaPagina.size());
        assertNotEquals(
                primeraPagina.get(0).getId(),
                segundaPagina.get(0).getId()
        );

        assertEquals(3L, usuarioPersistenceAdapter.contarCuentas(
                null, null, "laura"
        ));
    }

    @Test
    void debeEliminarFisicamenteUnUsuarioExistente() {

        Usuario usuario = crearUsuario(
                "Usuario para eliminar",
                "eliminar@gmail.com",
                passwordEncoder.encode("Password123"),
                false
        );

        Usuario guardado = usuarioPersistenceAdapter.guardar(usuario);
        Long usuarioId = guardado.getId();

        assertTrue(usuarioJpaRepository.existsById(usuarioId));

        boolean eliminado = usuarioPersistenceAdapter.eliminarPorId(usuarioId);

        assertTrue(eliminado);
        assertFalse(usuarioJpaRepository.existsById(usuarioId));
    }

    @Test
    void noDebeEliminarUnUsuarioInexistente() {

        boolean eliminado = usuarioPersistenceAdapter.eliminarPorId(-1L);

        assertFalse(eliminado);
        assertEquals(0, usuarioJpaRepository.count());
    }

    private Usuario crearUsuario(
            String nombre,
            String email,
            String passwordHash,
            Boolean correoVerificado) {

        return crearUsuario(
                nombre,
                email,
                passwordHash,
                correoVerificado,
                null,
                175.0,
                96.0,
                82.0,
                98.0
        );
    }

    private Usuario crearUsuario(
            String nombre,
            String email,
            String passwordHash,
            Boolean correoVerificado,
            String barrio) {

        return crearUsuario(
                nombre,
                email,
                passwordHash,
                correoVerificado,
                barrio,
                175.0,
                96.0,
                82.0,
                98.0
        );
    }

    private Usuario crearUsuario(
            String nombre,
            String email,
            String passwordHash,
            Boolean correoVerificado,
            String barrio,
            Double heightCm,
            Double chestCm,
            Double waistCm,
            Double hipCm) {

        return new Usuario(
                null,
                nombre,
                email,
                "+57 300 123 4567",
                passwordHash,
                true,
                Rol.COMPRADOR,
                EstadoCuenta.ACTIVA,
                Instant.now(),
                null,
                null,
                null,
                correoVerificado,
                barrio,
                heightCm,
                chestCm,
                waistCm,
                hipCm
        );
    }
}
