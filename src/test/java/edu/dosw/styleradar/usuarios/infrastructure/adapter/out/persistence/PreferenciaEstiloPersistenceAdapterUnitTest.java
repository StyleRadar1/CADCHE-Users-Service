package edu.dosw.styleradar.usuarios.infrastructure.adapter.out.persistence;

import edu.dosw.styleradar.usuarios.domain.exception.UsuarioNoEncontradoException;
import edu.dosw.styleradar.usuarios.domain.model.Estilo;
import edu.dosw.styleradar.usuarios.infrastructure.adapter.out.persistence.entity.PreferenciaEstiloEntity;
import edu.dosw.styleradar.usuarios.infrastructure.adapter.out.persistence.entity.UsuarioEntity;
import edu.dosw.styleradar.usuarios.infrastructure.adapter.out.persistence.repository.PreferenciaEstiloJpaRepository;
import edu.dosw.styleradar.usuarios.infrastructure.adapter.out.persistence.repository.UsuarioJpaRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PreferenciaEstiloPersistenceAdapterUnitTest {

    private PreferenciaEstiloJpaRepository preferenciaRepository;
    private UsuarioJpaRepository usuarioRepository;
    private PreferenciaEstiloPersistenceAdapter adapter;

    @BeforeEach
    void setUp() {
        preferenciaRepository = mock(PreferenciaEstiloJpaRepository.class);
        usuarioRepository = mock(UsuarioJpaRepository.class);

        adapter = new PreferenciaEstiloPersistenceAdapter(
                preferenciaRepository,
                usuarioRepository
        );
    }

    @Test
    void debeReemplazarPreferenciasDeEstilo() {
        UsuarioEntity usuario = new UsuarioEntity();

        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(usuario));

        Set<Estilo> preferencias = Set.of(
                Estilo.CASUAL,
                Estilo.URBANO,
                Estilo.VINTAGE
        );

        Set<Estilo> resultado =
                adapter.reemplazar(1L, preferencias);

        assertEquals(preferencias, resultado);

        verify(usuarioRepository).findById(1L);
        verify(preferenciaRepository).deleteByUsuario_Id(1L);
        verify(preferenciaRepository).flush();
    }

    @Test
    void debePermitirReemplazarConPreferenciasVacias() {
        UsuarioEntity usuario = new UsuarioEntity();

        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(usuario));

        Set<Estilo> resultado =
                adapter.reemplazar(1L, Set.of());

        assertTrue(resultado.isEmpty());

        verify(preferenciaRepository).deleteByUsuario_Id(1L);
        verify(preferenciaRepository).flush();
    }

    @Test
    void debeLanzarExcepcionSiUsuarioNoExiste() {
        when(usuarioRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                UsuarioNoEncontradoException.class,
                () -> adapter.reemplazar(
                        99L,
                        Set.of(Estilo.CASUAL)
                )
        );

        verify(preferenciaRepository, never())
                .deleteByUsuario_Id(anyLong());

        verify(preferenciaRepository, never()).flush();
    }

    @Test
    void debeBuscarPreferenciasPorUsuario() {
        UsuarioEntity usuario = new UsuarioEntity();

        List<PreferenciaEstiloEntity> entidades = List.of(
                new PreferenciaEstiloEntity(usuario, Estilo.CASUAL),
                new PreferenciaEstiloEntity(usuario, Estilo.URBANO)
        );

        when(preferenciaRepository.findByUsuario_Id(1L))
                .thenReturn(entidades);

        Set<Estilo> resultado =
                adapter.buscarPorUsuarioId(1L);

        assertEquals(
                Set.of(Estilo.CASUAL, Estilo.URBANO),
                resultado
        );

        verify(preferenciaRepository).findByUsuario_Id(1L);
    }

    @Test
    void debeRetornarConjuntoVacioCuandoNoHayPreferencias() {
        when(preferenciaRepository.findByUsuario_Id(1L))
                .thenReturn(List.of());

        Set<Estilo> resultado =
                adapter.buscarPorUsuarioId(1L);

        assertTrue(resultado.isEmpty());
    }
}
