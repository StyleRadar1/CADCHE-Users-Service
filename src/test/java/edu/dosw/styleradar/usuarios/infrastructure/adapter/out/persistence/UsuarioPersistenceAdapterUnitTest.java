package edu.dosw.styleradar.usuarios.infrastructure.adapter.out.persistence;

import edu.dosw.styleradar.usuarios.domain.exception.EmailYaRegistradoException;
import edu.dosw.styleradar.usuarios.domain.exception.IdentidadExternaConflictivaException;
import edu.dosw.styleradar.usuarios.domain.model.*;
import edu.dosw.styleradar.usuarios.infrastructure.adapter.out.persistence.entity.UsuarioEntity;
import edu.dosw.styleradar.usuarios.infrastructure.adapter.out.persistence.repository.UsuarioJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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
        return crearUsuario(false);
    }

    private Usuario crearUsuario(Boolean correoVerificado) {
        return new Usuario(
                1L,
                "Julian",
                "julian@gmail.com",
                "3001234567",
                "hash",
                true,
                Rol.COMPRADOR,
                EstadoCuenta.ACTIVA,
                Instant.now(),
                null,
                null,
                null,
                correoVerificado,
                "Chapinero"
        );
    }

    private UsuarioEntity crearEntity() {
        UsuarioEntity entity = new UsuarioEntity();
        entity.setId(1L);
        entity.setNombre("Julian");
        entity.setEmail("julian@gmail.com");
        entity.setTelefono("3001234567");
        entity.setBarrio("Chapinero");
        entity.setPasswordHash("hash");
        entity.setAceptaHabeasData(true);
        entity.setCorreoVerificado(false);
        entity.setProvider("GOOGLE");
        entity.setProviderId("google-sub-1");
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
        when(repository.saveAndFlush(any(UsuarioEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Usuario resultado = adapter.guardar(crearUsuario());

        ArgumentCaptor<UsuarioEntity> captor =
                ArgumentCaptor.forClass(UsuarioEntity.class);

        verify(repository).saveAndFlush(captor.capture());

        assertEquals(1L, resultado.getId());
        assertEquals("Julian", resultado.getNombre());
        assertEquals("julian@gmail.com", resultado.getEmail());
        assertEquals("Chapinero", resultado.getBarrio());
        assertEquals(Rol.COMPRADOR, resultado.getRol());

        // Comprueba que el valor false se guarda y se recupera.
        assertEquals(Boolean.FALSE, captor.getValue().getCorreoVerificado());
        assertEquals(Boolean.FALSE, resultado.getCorreoVerificado());
    }

    @Test
    void debeGuardarIdentidadExterna() {
        Usuario tradicional = crearUsuario();
        Usuario conGoogle = new Usuario(tradicional.getId(), tradicional.getNombre(),
                tradicional.getEmail(), tradicional.getTelefono(), tradicional.getPasswordHash(),
                tradicional.getAceptaHabeasData(), tradicional.getRol(), tradicional.getEstado(),
                tradicional.getFechaRegistro(), tradicional.getFechaCambioEstado(),
                tradicional.getAdministradorCambioEstadoId(), tradicional.getMotivoCambioEstado(),
                tradicional.getCorreoVerificado(), tradicional.getBarrio(), tradicional.getHeightCm(),
                tradicional.getChestCm(), tradicional.getWaistCm(), tradicional.getHipCm(),
                "GOOGLE", "google-sub-1");
        when(repository.saveAndFlush(any(UsuarioEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Usuario resultado = adapter.guardar(conGoogle);

        ArgumentCaptor<UsuarioEntity> captor = ArgumentCaptor.forClass(UsuarioEntity.class);
        verify(repository).saveAndFlush(captor.capture());
        assertEquals("GOOGLE", captor.getValue().getProvider());
        assertEquals("google-sub-1", captor.getValue().getProviderId());
        assertEquals("GOOGLE", resultado.getProvider());
        assertEquals("google-sub-1", resultado.getProviderId());
    }

    @Test
    void debeGuardarUsuarioConCorreoVerificado() {
        when(repository.saveAndFlush(any(UsuarioEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Usuario resultado = adapter.guardar(crearUsuario(true));

        ArgumentCaptor<UsuarioEntity> captor =
                ArgumentCaptor.forClass(UsuarioEntity.class);

        verify(repository).saveAndFlush(captor.capture());

        // Comprueba que el valor true se guarda y se recupera.
        assertEquals(Boolean.TRUE, captor.getValue().getCorreoVerificado());
        assertEquals(Boolean.TRUE, resultado.getCorreoVerificado());
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
        assertEquals(Boolean.FALSE, resultado.get().getCorreoVerificado());
    }

    @Test
    void debeRetornarVacioCuandoNoEncuentraUsuarioPorId() {
        when(repository.findById(2L)).thenReturn(Optional.empty());

        assertTrue(adapter.buscarPorId(2L).isEmpty());
    }

    @Test
    void actualizaNombreYConservaTelefonoYBarrioSiSeOmiten() {
        UsuarioEntity entity = crearEntity();
        when(repository.findById(1L)).thenReturn(Optional.of(entity));
        when(repository.saveAndFlush(entity)).thenReturn(entity);

        Usuario actualizado = adapter.actualizarDatosPersonales(
                1L, "Laura Gómez", null, null).orElseThrow();

        assertEquals("Laura Gómez", actualizado.getNombre());
        assertEquals("3001234567", actualizado.getTelefono());
        assertEquals("Chapinero", actualizado.getBarrio());
        verify(repository).saveAndFlush(entity);
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
    void debeBuscarUsuarioPorIdentidadExterna() {
        when(repository.findByProviderAndProviderId("GOOGLE", "google-sub-1"))
                .thenReturn(Optional.of(crearEntity()));

        Optional<Usuario> resultado = adapter.buscarPorProveedorYId("GOOGLE", "google-sub-1");

        assertTrue(resultado.isPresent());
        assertEquals("GOOGLE", resultado.get().getProvider());
        assertEquals("google-sub-1", resultado.get().getProviderId());
    }

    @Test
    void debeVincularIdentidadGoogleAUsuarioSinProveedor() {
        UsuarioEntity entity = crearEntity();
        entity.setProvider(null);
        entity.setProviderId(null);
        when(repository.vincularGoogleSiNoTieneIdentidad(1L, "google-sub-1"))
                .thenAnswer(invocation -> {
                    entity.setProvider("GOOGLE");
                    entity.setProviderId(invocation.getArgument(1));
                    return 1;
                });
        when(repository.findById(1L)).thenReturn(Optional.of(entity));

        Optional<Usuario> resultado = adapter.vincularIdentidadGoogle(1L, "google-sub-1");

        assertTrue(resultado.isPresent());
        assertEquals("GOOGLE", resultado.get().getProvider());
        assertEquals("google-sub-1", resultado.get().getProviderId());
        verify(repository).vincularGoogleSiNoTieneIdentidad(1L, "google-sub-1");
    }

    @Test
    void devuelveVacioAlVincularIdentidadSiElUsuarioNoExiste() {
        when(repository.vincularGoogleSiNoTieneIdentidad(99L, "google-sub-1"))
                .thenReturn(0);
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertTrue(adapter.vincularIdentidadGoogle(99L, "google-sub-1").isEmpty());
    }

    @Test
    void convierteConflictoDeUnicidadAlVincularIdentidadGoogle() {
        when(repository.vincularGoogleSiNoTieneIdentidad(1L, "google-sub-1"))
                .thenThrow(new DataIntegrityViolationException("Identidad duplicada"));

        assertThrows(IdentidadExternaConflictivaException.class,
                () -> adapter.vincularIdentidadGoogle(1L, "google-sub-1"));
        verify(repository, never()).findById(1L);
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
