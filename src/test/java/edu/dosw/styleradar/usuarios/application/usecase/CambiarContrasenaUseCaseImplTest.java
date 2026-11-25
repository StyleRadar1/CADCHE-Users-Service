package edu.dosw.styleradar.usuarios.application.usecase;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import edu.dosw.styleradar.usuarios.domain.exception.ContrasenaActualIncorrectaException;
import edu.dosw.styleradar.usuarios.domain.exception.CuentaNoActivaException;
import edu.dosw.styleradar.usuarios.domain.exception.UsuarioNoEncontradoException;
import edu.dosw.styleradar.usuarios.domain.model.EstadoCuenta;
import edu.dosw.styleradar.usuarios.domain.model.Rol;
import edu.dosw.styleradar.usuarios.domain.model.Usuario;
import edu.dosw.styleradar.usuarios.domain.port.out.UsuarioRepositoryPort;
import edu.dosw.styleradar.usuarios.domain.port.out.UsuarioContrasenaCambiadaEventPublisherPort;

class CambiarContrasenaUseCaseImplTest {

    private UsuarioRepositoryPort repository;
    private PasswordEncoder passwordEncoder;
    private UsuarioContrasenaCambiadaEventPublisherPort eventPublisher;
    private CambiarContrasenaUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        repository = org.mockito.Mockito.mock(UsuarioRepositoryPort.class);
        passwordEncoder = org.mockito.Mockito.mock(PasswordEncoder.class);
        eventPublisher = org.mockito.Mockito.mock(UsuarioContrasenaCambiadaEventPublisherPort.class);
        useCase = new CambiarContrasenaUseCaseImpl(repository, passwordEncoder, eventPublisher);
    }

    @Test
    void verificaLaActualYGuardaElHashDeLaNueva() {
        when(repository.buscarPorId(7L)).thenReturn(Optional.of(usuario("hash-actual")));
        when(passwordEncoder.matches("Actual123", "hash-actual")).thenReturn(true);
        when(passwordEncoder.encode("Nueva1234")).thenReturn("hash-nuevo");
        when(repository.actualizarPasswordHash(7L, "hash-nuevo")).thenReturn(true);

        assertDoesNotThrow(() -> useCase.ejecutar(7L, "Actual123", "Nueva1234"));

        verify(passwordEncoder).matches("Actual123", "hash-actual");
        verify(passwordEncoder).encode("Nueva1234");
        verify(repository).actualizarPasswordHash(7L, "hash-nuevo");
        verify(eventPublisher).publicar(org.mockito.ArgumentMatchers.argThat(
                event -> event.userId().equals(7L) && event.eventId() != null && event.changedAt() != null));
    }

    @Test
    void rechazaLaContrasenaActualIncorrecta() {
        when(repository.buscarPorId(7L)).thenReturn(Optional.of(usuario("hash-actual")));
        when(passwordEncoder.matches("incorrecta", "hash-actual")).thenReturn(false);

        assertThrows(ContrasenaActualIncorrectaException.class,
                () -> useCase.ejecutar(7L, "incorrecta", "Nueva1234"));
        verify(passwordEncoder, never()).encode("Nueva1234");
        verify(repository, never()).actualizarPasswordHash(7L, "hash-nuevo");
        verify(eventPublisher, never()).publicar(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void rechazaCuentaSinHashDeContrasena() {
        when(repository.buscarPorId(7L)).thenReturn(Optional.of(usuario(null)));

        assertThrows(ContrasenaActualIncorrectaException.class,
                () -> useCase.ejecutar(7L, "Actual123", "Nueva1234"));
        verify(passwordEncoder, never()).matches("Actual123", null);
        verify(eventPublisher, never()).publicar(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void devuelve404SiLaCuentaNoExiste() {
        when(repository.buscarPorId(404L)).thenReturn(Optional.empty());

        assertThrows(UsuarioNoEncontradoException.class,
                () -> useCase.ejecutar(404L, "Actual123", "Nueva1234"));
    }

    @Test
    void devuelve404SiLaCuentaDesapareceAntesDeActualizarElHash() {
        when(repository.buscarPorId(7L)).thenReturn(Optional.of(usuario("hash-actual")));
        when(passwordEncoder.matches("Actual123", "hash-actual")).thenReturn(true);
        when(passwordEncoder.encode("Nueva1234")).thenReturn("hash-nuevo");
        when(repository.actualizarPasswordHash(7L, "hash-nuevo")).thenReturn(false);

        assertThrows(UsuarioNoEncontradoException.class,
                () -> useCase.ejecutar(7L, "Actual123", "Nueva1234"));
        verify(eventPublisher, never()).publicar(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void restableceContrasenaDeCuentaActivaYPublicaEvento() {
        when(repository.buscarPorId(7L)).thenReturn(Optional.of(usuario("hash-anterior")));
        when(passwordEncoder.encode("Nueva1234")).thenReturn("hash-nuevo");
        when(repository.actualizarPasswordHash(7L, "hash-nuevo")).thenReturn(true);

        assertDoesNotThrow(() -> useCase.restablecerInternamente(7L, "Nueva1234"));

        verify(repository).actualizarPasswordHash(7L, "hash-nuevo");
        verify(eventPublisher).publicar(org.mockito.ArgumentMatchers.argThat(
                event -> event.userId().equals(7L)));
    }

    @Test
    void rechazaRestablecimientoDeCuentaInactivaSinPublicarEvento() {
        Usuario inactivo = new Usuario(7L, "Cuenta", "cuenta@example.com", null, "hash-actual",
                true, Rol.COMPRADOR, EstadoCuenta.INACTIVA,
                Instant.parse("2026-11-20T10:00:00Z"));
        when(repository.buscarPorId(7L)).thenReturn(Optional.of(inactivo));

        assertThrows(CuentaNoActivaException.class,
                () -> useCase.restablecerInternamente(7L, "Nueva1234"));

        verify(passwordEncoder, never()).encode("Nueva1234");
        verify(repository, never()).actualizarPasswordHash(7L, "hash-nuevo");
        verify(eventPublisher, never()).publicar(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void devuelve404AlRestablecerContrasenaDeUsuarioInexistente() {
        when(repository.buscarPorId(404L)).thenReturn(Optional.empty());

        assertThrows(UsuarioNoEncontradoException.class,
                () -> useCase.restablecerInternamente(404L, "Nueva1234"));

        verify(passwordEncoder, never()).encode("Nueva1234");
        verify(eventPublisher, never()).publicar(org.mockito.ArgumentMatchers.any());
    }

    private Usuario usuario(String hash) {
        return new Usuario(7L, "Cuenta", "cuenta@example.com", null, hash,
                true, Rol.COMPRADOR, EstadoCuenta.ACTIVA,
                Instant.parse("2026-11-20T10:00:00Z"));
    }
}
