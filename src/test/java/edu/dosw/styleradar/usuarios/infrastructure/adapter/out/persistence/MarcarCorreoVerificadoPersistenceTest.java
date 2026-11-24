package edu.dosw.styleradar.usuarios.infrastructure.adapter.out.persistence;

import edu.dosw.styleradar.usuarios.domain.model.EstadoCuenta;
import edu.dosw.styleradar.usuarios.domain.model.Rol;
import edu.dosw.styleradar.usuarios.infrastructure.adapter.out.persistence.entity.UsuarioEntity;
import edu.dosw.styleradar.usuarios.infrastructure.adapter.out.persistence.repository.UsuarioJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MarcarCorreoVerificadoPersistenceTest {

    private UsuarioJpaRepository repository;
    private UsuarioPersistenceAdapter adapter;

    @BeforeEach
    void setUp() {
        repository = mock(UsuarioJpaRepository.class);
        adapter = new UsuarioPersistenceAdapter(repository);
    }

    private UsuarioEntity crearEntity() {
        UsuarioEntity entity = new UsuarioEntity();
        entity.setId(144L);
        entity.setNombre("Usuario de prueba");
        entity.setEmail("usuario@gmail.com");
        entity.setTelefono("3001234567");
        entity.setPasswordHash("hash");
        entity.setAceptaHabeasData(true);
        entity.setCorreoVerificado(false);
        entity.setRol(Rol.COMPRADOR);
        entity.setEstado(EstadoCuenta.ACTIVA);
        entity.setFechaRegistro(Instant.now());
        return entity;
    }

    @Test
    void debeGuardarCorreoComoVerificado() {

        UsuarioEntity entity = crearEntity();

        when(repository.findById(144L)).thenReturn(Optional.of(entity));
        when(repository.saveAndFlush(entity)).thenReturn(entity);

        var resultado = adapter.marcarCorreoComoVerificado(144L);

        assertTrue(resultado.isPresent());
        assertTrue(entity.getCorreoVerificado());
        assertTrue(resultado.get().getCorreoVerificado());

        verify(repository).findById(144L);
        verify(repository).saveAndFlush(entity);
    }

    @Test
    void noDebeGuardarSiUsuarioNoExiste() {

        when(repository.findById(999L)).thenReturn(Optional.empty());

        var resultado = adapter.marcarCorreoComoVerificado(999L);

        assertTrue(resultado.isEmpty());

        verify(repository).findById(999L);
        verify(repository, never()).saveAndFlush(any());
    }
}
