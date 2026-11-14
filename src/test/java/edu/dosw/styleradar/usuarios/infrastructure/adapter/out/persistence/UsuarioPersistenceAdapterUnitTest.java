package edu.dosw.styleradar.usuarios.infrastructure.adapter.out.persistence;

import edu.dosw.styleradar.usuarios.domain.exception.EmailYaRegistradoException;
import edu.dosw.styleradar.usuarios.domain.model.*;
import edu.dosw.styleradar.usuarios.infrastructure.adapter.out.persistence.entity.UsuarioEntity;
import edu.dosw.styleradar.usuarios.infrastructure.adapter.out.persistence.repository.UsuarioJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UsuarioPersistenceAdapterUnitTest {

    private UsuarioJpaRepository repository;
    private UsuarioPersistenceAdapter adapter;

    @BeforeEach
    void setUp() {
        repository = mock(UsuarioJpaRepository.class);
        adapter = new UsuarioPersistenceAdapter(repository);
    }

    private Usuario crearUsuario() {
        return new Usuario(
                1L, "Julian", "julian@gmail.com", "3001234567",
                "hash", true, Rol.COMPRADOR, EstadoCuenta.ACTIVA,
                Instant.now()
        );
    }

    private UsuarioEntity crearEntity() {
        UsuarioEntity entity = new UsuarioEntity();
        entity.setId(1L);
        entity.setNombre("Julian");
        entity.setEmail("julian@gmail.com");
        entity.setTelefono("3001234567");
        entity.setPasswordHash("hash");
        entity.setAceptaHabeasData(true);
        entity.setRol(Rol.COMPRADOR);
        entity.setEstado(EstadoCuenta.ACTIVA);
        entity.setFechaRegistro(Instant.now());
        return entity;
    }

    @Test
    void debeVerificarSiExisteEmail() {
        when(repository.existsByEmail("julian@gmail.com")).thenReturn(true);

        assertTrue(adapter.existePorEmail("julian@gmail.com"));
        verify(repository).existsByEmail("julian@gmail.com");
    }

    @Test
    void debeGuardarUsuario() {
        UsuarioEntity entity = crearEntity();
        when(repository.saveAndFlush(any(UsuarioEntity.class))).thenReturn(entity);

        Usuario resultado = adapter.guardar(crearUsuario());

        assertEquals(1L, resultado.getId());
        assertEquals("Julian", resultado.getNombre());
        assertEquals("julian@gmail.com", resultado.getEmail());
        assertEquals(Rol.COMPRADOR, resultado.getRol());
        verify(repository).saveAndFlush(any(UsuarioEntity.class));
    }

    @Test
    void debeConvertirErrorDeIntegridadEnEmailDuplicado() {
        when(repository.saveAndFlush(any(UsuarioEntity.class)))
                .thenThrow(new DataIntegrityViolationException("Duplicado"));

        assertThrows(
                EmailYaRegistradoException.class,
                () -> adapter.guardar(crearUsuario())
        );
    }

    @Test
    void debeBuscarUsuarioPorId() {
        when(repository.findById(1L)).thenReturn(Optional.of(crearEntity()));

        Optional<Usuario> resultado = adapter.buscarPorId(1L);

        assertTrue(resultado.isPresent());
        assertEquals("Julian", resultado.get().getNombre());
    }

    @Test
    void debeRetornarVacioCuandoNoEncuentraUsuarioPorId() {
        when(repository.findById(2L)).thenReturn(Optional.empty());

        assertTrue(adapter.buscarPorId(2L).isEmpty());
    }

    @Test
    void debeBuscarUsuarioPorEmail() {
        when(repository.findByEmail("julian@gmail.com"))
                .thenReturn(Optional.of(crearEntity()));

        Optional<Usuario> resultado = adapter.buscarPorEmail("julian@gmail.com");

        assertTrue(resultado.isPresent());
        assertEquals("julian@gmail.com", resultado.get().getEmail());
    }

    @Test
    void debeRetornarVacioCuandoNoEncuentraUsuarioPorEmail() {
        when(repository.findByEmail("otro@gmail.com"))
                .thenReturn(Optional.empty());

        assertTrue(adapter.buscarPorEmail("otro@gmail.com").isEmpty());
    }

    @Test
    void debeContarAdministradoresActivos() {
        when(repository.countByRolAndEstado(
                Rol.ADMIN_STYLERADAR, EstadoCuenta.ACTIVA
        )).thenReturn(3L);

        assertEquals(3L, adapter.contarAdministradoresActivos());
    }

    @Test
    void debeCambiarEstadoDelUsuario() {
        UsuarioEntity entity = crearEntity();

        when(repository.findById(1L)).thenReturn(Optional.of(entity));
        when(repository.saveAndFlush(entity)).thenReturn(entity);

        Instant fecha = Instant.now();

        Optional<Usuario> resultado = adapter.cambiarEstado(
                1L, EstadoCuenta.BLOQUEADA, fecha, 10L, "Incumplimiento"
        );

        assertTrue(resultado.isPresent());
        assertEquals(EstadoCuenta.BLOQUEADA, resultado.get().getEstado());
        assertEquals(fecha, entity.getFechaCambioEstado());
        assertEquals(10L, entity.getAdministradorCambioEstadoId());
        assertEquals("Incumplimiento", entity.getMotivoCambioEstado());
    }

    @Test
    void noDebeCambiarEstadoSiUsuarioNoExiste() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        Optional<Usuario> resultado = adapter.cambiarEstado(
                99L, EstadoCuenta.BLOQUEADA, Instant.now(), 10L, "Motivo"
        );

        assertTrue(resultado.isEmpty());
        verify(repository, never()).saveAndFlush(any());
    }
}
