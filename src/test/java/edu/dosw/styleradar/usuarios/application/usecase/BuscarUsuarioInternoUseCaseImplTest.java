package edu.dosw.styleradar.usuarios.application.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.dosw.styleradar.usuarios.domain.exception.UsuarioNoEncontradoPorEmailException;
import edu.dosw.styleradar.usuarios.domain.model.EstadoCuenta;
import edu.dosw.styleradar.usuarios.domain.model.Rol;
import edu.dosw.styleradar.usuarios.domain.model.Usuario;
import edu.dosw.styleradar.usuarios.domain.port.out.UsuarioRepositoryPort;

class BuscarUsuarioInternoUseCaseImplTest {

    private UsuarioRepositoryPort repository;
    private BuscarUsuarioInternoUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        repository = org.mockito.Mockito.mock(UsuarioRepositoryPort.class);
        useCase = new BuscarUsuarioInternoUseCaseImpl(repository);
    }

    @Test
    void normalizaCorreoYDevuelveLaCuentaEncontrada() {
        Usuario usuario = new Usuario(12L, "Cuenta", "user@example.com", null, "hash",
                true, Rol.COMPRADOR, EstadoCuenta.ACTIVA,
                Instant.parse("2026-11-20T10:00:00Z"));
        when(repository.buscarPorEmail("user@example.com")).thenReturn(Optional.of(usuario));

        Usuario resultado = useCase.ejecutar(" User@Example.com ");

        assertEquals(usuario, resultado);
        verify(repository).buscarPorEmail("user@example.com");
    }

    @Test
    void arrojaNoEncontradoCuandoElCorreoNoExiste() {
        when(repository.buscarPorEmail("missing@example.com")).thenReturn(Optional.empty());

        assertThrows(UsuarioNoEncontradoPorEmailException.class,
                () -> useCase.ejecutar("missing@example.com"));
    }
}
