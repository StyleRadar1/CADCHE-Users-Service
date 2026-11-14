package edu.dosw.styleradar.usuarios.infrastructure.adapter.out.persistence;

import edu.dosw.styleradar.usuarios.domain.exception.UsuarioNoEncontradoException;
import edu.dosw.styleradar.usuarios.domain.model.TipoPrenda;
import edu.dosw.styleradar.usuarios.infrastructure.adapter.out.persistence.entity.TallaEntity;
import edu.dosw.styleradar.usuarios.infrastructure.adapter.out.persistence.entity.UsuarioEntity;
import edu.dosw.styleradar.usuarios.infrastructure.adapter.out.persistence.repository.TallaJpaRepository;
import edu.dosw.styleradar.usuarios.infrastructure.adapter.out.persistence.repository.UsuarioJpaRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TallaPersistenceAdapterUnitTest {

    private TallaJpaRepository tallaRepository;
    private UsuarioJpaRepository usuarioRepository;
    private TallaPersistenceAdapter adapter;

    @BeforeEach
    void setUp() {
        tallaRepository = mock(TallaJpaRepository.class);
        usuarioRepository = mock(UsuarioJpaRepository.class);

        adapter = new TallaPersistenceAdapter(
                tallaRepository,
                usuarioRepository
        );
    }

    @Test
    void debeReemplazarTallas() {
        UsuarioEntity usuario = new UsuarioEntity();

        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(usuario));

        Map<TipoPrenda, Set<String>> tallas = Map.of(
                TipoPrenda.SUPERIOR, Set.of("M", "L"),
                TipoPrenda.INFERIOR, Set.of("32")
        );

        Map<TipoPrenda, Set<String>> resultado =
                adapter.reemplazar(1L, tallas);

        assertEquals(tallas, resultado);

        verify(tallaRepository).deleteByUsuario_Id(1L);
        verify(tallaRepository).flush();

        verify(tallaRepository).saveAllAndFlush(
                ArgumentMatchers.<Iterable<TallaEntity>>any()
        );
    }

    @Test
    void debePermitirReemplazarConMapaVacio() {
        UsuarioEntity usuario = new UsuarioEntity();

        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(usuario));

        Map<TipoPrenda, Set<String>> resultado =
                adapter.reemplazar(1L, Map.of());

        assertTrue(resultado.isEmpty());

        verify(tallaRepository).deleteByUsuario_Id(1L);
        verify(tallaRepository).flush();

        verify(tallaRepository).saveAllAndFlush(
                ArgumentMatchers.<Iterable<TallaEntity>>any()
        );
    }

    @Test
    void debeLanzarExcepcionSiUsuarioNoExiste() {
        when(usuarioRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                UsuarioNoEncontradoException.class,
                () -> adapter.reemplazar(99L, Map.of())
        );

        verify(tallaRepository, never()).deleteByUsuario_Id(any());
        verify(tallaRepository, never()).saveAllAndFlush(
                ArgumentMatchers.<Iterable<TallaEntity>>any()
        );
    }

    @Test
    void debeBuscarYAgruparTallasPorTipoDePrenda() {
        UsuarioEntity usuario = new UsuarioEntity();

        List<TallaEntity> entidades = List.of(
                new TallaEntity(usuario, TipoPrenda.SUPERIOR, "M"),
                new TallaEntity(usuario, TipoPrenda.SUPERIOR, "L"),
                new TallaEntity(usuario, TipoPrenda.INFERIOR, "32")
        );

        when(tallaRepository.findByUsuario_Id(1L))
                .thenReturn(entidades);

        Map<TipoPrenda, Set<String>> resultado =
                adapter.buscarPorUsuarioId(1L);

        assertEquals(
                Set.of("M", "L"),
                resultado.get(TipoPrenda.SUPERIOR)
        );

        assertEquals(
                Set.of("32"),
                resultado.get(TipoPrenda.INFERIOR)
        );

        assertEquals(2, resultado.size());
    }

    @Test
    void debeRetornarMapaVacioCuandoNoHayTallas() {
        when(tallaRepository.findByUsuario_Id(1L))
                .thenReturn(List.of());

        Map<TipoPrenda, Set<String>> resultado =
                adapter.buscarPorUsuarioId(1L);

        assertTrue(resultado.isEmpty());
    }
}
