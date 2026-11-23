package edu.dosw.styleradar.usuarios.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import edu.dosw.styleradar.usuarios.application.dto.request.RegistrarUsuarioGoogleRequestDTO;
import edu.dosw.styleradar.usuarios.application.usecase.RegistrarUsuarioGoogleUseCaseImpl;
import edu.dosw.styleradar.usuarios.domain.exception.IdentidadExternaConflictivaException;
import edu.dosw.styleradar.usuarios.domain.model.EstadoCuenta;
import edu.dosw.styleradar.usuarios.domain.model.Rol;
import edu.dosw.styleradar.usuarios.domain.model.Usuario;
import edu.dosw.styleradar.usuarios.domain.port.out.UsuarioRepositoryPort;

class RegistrarUsuarioGoogleUseCaseTest {

    private UsuarioRepositoryPort repository;
    private PasswordEncoder passwordEncoder;
    private RegistrarUsuarioGoogleUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        repository = org.mockito.Mockito.mock(UsuarioRepositoryPort.class);
        passwordEncoder = new BCryptPasswordEncoder();
        useCase = new RegistrarUsuarioGoogleUseCaseImpl(repository, passwordEncoder);
    }

    @Test
    void creaCompradorActivoVerificadoSinConsentimientoYConHashAleatorio() {
        when(repository.buscarPorEmail("usuario@gmail.com")).thenReturn(Optional.empty());
        when(repository.buscarPorProveedorYId("GOOGLE", "google-sub-1"))
                .thenReturn(Optional.empty());
        when(repository.guardar(any(Usuario.class))).thenAnswer(call -> call.getArgument(0));

        Usuario actual = useCase.ejecutar(request());

        assertEquals("Usuario de Google", actual.getNombre());
        assertEquals("usuario@gmail.com", actual.getEmail());
        assertEquals(Rol.COMPRADOR, actual.getRol());
        assertEquals(EstadoCuenta.ACTIVA, actual.getEstado());
        assertEquals(Boolean.TRUE, actual.getCorreoVerificado());
        assertEquals(Boolean.FALSE, actual.getAceptaHabeasData());
        assertEquals("GOOGLE", actual.getProvider());
        assertEquals("google-sub-1", actual.getProviderId());
        assertNotNull(actual.getPasswordHash());
        assertTrue(actual.getPasswordHash().startsWith("$2"));
        assertTrue(!passwordEncoder.matches("StyleRadar2026*", actual.getPasswordHash()));
    }

    @ParameterizedTest
    @EnumSource(value = EstadoCuenta.class, names = {"INACTIVA", "BLOQUEADA"})
    void devuelveUsuarioExistenteSinReactivarNiCambiarRolConsentimientoOEstado(EstadoCuenta estado) {
        Usuario existente = usuario(8L, "GOOGLE", "google-sub-1", estado);
        existente = usuarioConDatos(existente, Rol.ADMIN_ALMACEN, true, false);
        when(repository.buscarPorEmail("usuario@gmail.com")).thenReturn(Optional.of(existente));

        Usuario actual = useCase.ejecutar(request());

        assertEquals(estado, actual.getEstado());
        assertEquals(Rol.ADMIN_ALMACEN, actual.getRol());
        assertEquals(Boolean.TRUE, actual.getAceptaHabeasData());
        assertEquals(Boolean.FALSE, actual.getCorreoVerificado());
        verify(repository, never()).guardar(any(Usuario.class));
        verify(repository, never()).vincularIdentidadGoogle(8L, "google-sub-1");
    }

    @Test
    void vinculaCuentaTradicionalSinAlterarEstadoRolNiConsentimiento() {
        Usuario existente = usuario(8L, null, null, EstadoCuenta.INACTIVA);
        existente = usuarioConDatos(existente, Rol.ADMIN_ALMACEN, true, false);
        Usuario vinculado = usuarioConDatos(usuario(8L, "GOOGLE", "google-sub-1", EstadoCuenta.INACTIVA),
                Rol.ADMIN_ALMACEN, true, false);
        when(repository.buscarPorEmail("usuario@gmail.com")).thenReturn(Optional.of(existente));
        when(repository.vincularIdentidadGoogle(8L, "google-sub-1"))
                .thenReturn(Optional.of(vinculado));

        Usuario actual = useCase.ejecutar(request());

        assertEquals("GOOGLE", actual.getProvider());
        assertEquals("google-sub-1", actual.getProviderId());
        assertEquals(EstadoCuenta.INACTIVA, actual.getEstado());
        assertEquals(Rol.ADMIN_ALMACEN, actual.getRol());
        assertEquals(Boolean.TRUE, actual.getAceptaHabeasData());
        assertEquals(Boolean.FALSE, actual.getCorreoVerificado());
        verify(repository, never()).guardar(any(Usuario.class));
    }

    @Test
    void rechazaIdentidadGoogleDistintaParaCuentaYaVinculada() {
        Usuario existente = usuario(8L, "GOOGLE", "otro-sub", EstadoCuenta.ACTIVA);
        when(repository.buscarPorEmail("usuario@gmail.com")).thenReturn(Optional.of(existente));

        assertThrows(IdentidadExternaConflictivaException.class,
                () -> useCase.ejecutar(request()));
        verify(repository, never()).vincularIdentidadGoogle(8L, "google-sub-1");
    }

    @Test
    void rechazaProviderIdYaAsociadoAOtroCorreo() {
        when(repository.buscarPorEmail("usuario@gmail.com")).thenReturn(Optional.empty());
        when(repository.buscarPorProveedorYId("GOOGLE", "google-sub-1"))
                .thenReturn(Optional.of(usuario(9L, "GOOGLE", "google-sub-1", EstadoCuenta.ACTIVA)));

        assertThrows(IdentidadExternaConflictivaException.class,
                () -> useCase.ejecutar(request()));
        verify(repository, never()).guardar(any(Usuario.class));
    }

    @Test
    void rechazaProveedorNoPermitidoAntesDeConsultarRepositorio() {
        RegistrarUsuarioGoogleRequestDTO request = new RegistrarUsuarioGoogleRequestDTO(
                "usuario@gmail.com", "Usuario", "FACEBOOK", "provider-sub");

        assertThrows(IllegalArgumentException.class, () -> useCase.ejecutar(request));
        verify(repository, never()).buscarPorEmail(any());
    }

    private RegistrarUsuarioGoogleRequestDTO request() {
        return new RegistrarUsuarioGoogleRequestDTO(
                " Usuario@GMAIL.COM ", " Usuario de Google ", "GOOGLE", " google-sub-1 ");
    }

    private Usuario usuario(Long id, String provider, String providerId, EstadoCuenta estado) {
        return new Usuario(id, "Usuario", "usuario@gmail.com", null, "hash", false,
                Rol.COMPRADOR, estado, Instant.now(), null, null, null, false, null,
                null, null, null, null, provider, providerId);
    }

    private Usuario usuarioConDatos(Usuario usuario, Rol rol, Boolean acepta, Boolean verificado) {
        return new Usuario(usuario.getId(), usuario.getNombre(), usuario.getEmail(),
                usuario.getTelefono(), usuario.getPasswordHash(), acepta, rol, usuario.getEstado(),
                usuario.getFechaRegistro(), usuario.getFechaCambioEstado(),
                usuario.getAdministradorCambioEstadoId(), usuario.getMotivoCambioEstado(),
                verificado, usuario.getBarrio(), usuario.getHeightCm(), usuario.getChestCm(),
                usuario.getWaistCm(), usuario.getHipCm(), usuario.getProvider(), usuario.getProviderId());
    }
}
