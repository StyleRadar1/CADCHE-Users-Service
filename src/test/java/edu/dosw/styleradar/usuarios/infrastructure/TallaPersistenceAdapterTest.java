package edu.dosw.styleradar.usuarios.infrastructure;

import edu.dosw.styleradar.usuarios.domain.model.EstadoCuenta;
import edu.dosw.styleradar.usuarios.domain.model.Rol;
import edu.dosw.styleradar.usuarios.domain.model.TipoPrenda;
import edu.dosw.styleradar.usuarios.domain.model.Usuario;
import edu.dosw.styleradar.usuarios.infrastructure.adapter.out.persistence.TallaPersistenceAdapter;
import edu.dosw.styleradar.usuarios.infrastructure.adapter.out.persistence.UsuarioPersistenceAdapter;
import edu.dosw.styleradar.usuarios.infrastructure.adapter.out.persistence.repository.TallaJpaRepository;
import edu.dosw.styleradar.usuarios.infrastructure.adapter.out.persistence.repository.UsuarioJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = {
        "INTERNAL_AUTH_USERNAME=usuario-de-prueba",
        "INTERNAL_AUTH_PASSWORD=password-de-prueba"
})
@ActiveProfiles("integration")
class TallaPersistenceAdapterTest {

    @Autowired
    private TallaPersistenceAdapter tallaPersistenceAdapter;

    @Autowired
    private UsuarioPersistenceAdapter usuarioPersistenceAdapter;

    @Autowired
    private TallaJpaRepository tallaJpaRepository;

    @Autowired
    private UsuarioJpaRepository usuarioJpaRepository;

    private Long usuarioId;

    @BeforeEach
    void setUp() {
        tallaJpaRepository.deleteAll();
        usuarioJpaRepository.deleteAll();

        Usuario usuario = new Usuario(
                null,
                "Julian Giral",
                "julian.tallas@gmail.com",
                "+57 300 123 4567",
                "password-hash",
                true,
                Rol.COMPRADOR,
                EstadoCuenta.ACTIVA,
                Instant.now()
        );

        Usuario guardado = usuarioPersistenceAdapter.guardar(usuario);
        usuarioId = guardado.getId();
    }

    @Test
    void debeGuardarTallasHabitualesEnPostgreSQL() {
        Map<TipoPrenda, Set<String>> tallas = Map.of(
                TipoPrenda.SUPERIOR, Set.of("M"),
                TipoPrenda.INFERIOR, Set.of("32"),
                TipoPrenda.CALZADO, Set.of("40")
        );

        Map<TipoPrenda, Set<String>> resultado =
                tallaPersistenceAdapter.reemplazar(usuarioId, tallas);

        assertEquals(tallas, resultado);

        var tallasGuardadas = tallaJpaRepository.findByUsuario_Id(usuarioId);

        assertEquals(3, tallasGuardadas.size());
        assertTrue(tallasGuardadas.stream().anyMatch(t ->
                t.getTipoPrenda() == TipoPrenda.SUPERIOR
                        && t.getTalla().equals("M")));

        assertTrue(tallasGuardadas.stream().anyMatch(t ->
                t.getTipoPrenda() == TipoPrenda.INFERIOR
                        && t.getTalla().equals("32")));

        assertTrue(tallasGuardadas.stream().anyMatch(t ->
                t.getTipoPrenda() == TipoPrenda.CALZADO
                        && t.getTalla().equals("40")));
    }

    @Test
    void debeReemplazarLasTallasAnterioresDelUsuario() {
        Map<TipoPrenda, Set<String>> tallasIniciales = Map.of(
                TipoPrenda.SUPERIOR, Set.of("M"),
                TipoPrenda.INFERIOR, Set.of("32")
        );

        tallaPersistenceAdapter.reemplazar(usuarioId, tallasIniciales);

        Map<TipoPrenda, Set<String>> nuevasTallas = Map.of(
                TipoPrenda.SUPERIOR, Set.of("L"),
                TipoPrenda.CALZADO, Set.of("40")
        );

        tallaPersistenceAdapter.reemplazar(usuarioId, nuevasTallas);

        var tallasGuardadas = tallaJpaRepository.findByUsuario_Id(usuarioId);

        assertEquals(2, tallasGuardadas.size());
        assertTrue(tallasGuardadas.stream().anyMatch(t ->
                t.getTipoPrenda() == TipoPrenda.SUPERIOR
                        && t.getTalla().equals("L")));

        assertTrue(tallasGuardadas.stream().anyMatch(t ->
                t.getTipoPrenda() == TipoPrenda.CALZADO
                        && t.getTalla().equals("40")));

        assertFalse(tallasGuardadas.stream().anyMatch(t ->
                t.getTalla().equals("M") || t.getTalla().equals("32")));
    }
}