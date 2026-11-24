package edu.dosw.styleradar.usuarios.application.usecase;

import edu.dosw.styleradar.usuarios.domain.event.UsuarioCuentaEliminadaEvent;
import edu.dosw.styleradar.usuarios.domain.exception.UsuarioNoEncontradoException;
import edu.dosw.styleradar.usuarios.domain.port.out.UsuarioCuentaEliminadaEventPublisherPort;
import edu.dosw.styleradar.usuarios.domain.port.out.UsuarioRepositoryPort;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EliminarUsuarioUseCaseImplTest {

    private UsuarioRepositoryPort usuarioRepository;
    private UsuarioCuentaEliminadaEventPublisherPort eventoPublisher;
    private EliminarUsuarioUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        usuarioRepository = mock(UsuarioRepositoryPort.class);
        eventoPublisher = mock(UsuarioCuentaEliminadaEventPublisherPort.class);

        useCase = new EliminarUsuarioUseCaseImpl(
                usuarioRepository,
                eventoPublisher
        );

        TransactionSynchronizationManager.initSynchronization();
    }

    @AfterEach
    void limpiarTransaccion() {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.clearSynchronization();
        }
    }

    @Test
    void debeEliminarUsuarioYPublicarEventoDespuesDelCommit() {

        when(usuarioRepository.eliminarPorId(501L))
                .thenReturn(true);

        useCase.ejecutar(501L);

        verify(usuarioRepository).eliminarPorId(501L);

        // El evento todavía no debe publicarse antes del commit.
        verifyNoInteractions(eventoPublisher);

        List<TransactionSynchronization> sincronizaciones =
                TransactionSynchronizationManager.getSynchronizations();

        assertEquals(1, sincronizaciones.size());

        // Simulamos que la transacción fue confirmada.
        sincronizaciones.get(0).afterCommit();

        ArgumentCaptor<UsuarioCuentaEliminadaEvent> captor =
                ArgumentCaptor.forClass(UsuarioCuentaEliminadaEvent.class);

        verify(eventoPublisher).publicar(captor.capture());

        UsuarioCuentaEliminadaEvent evento = captor.getValue();

        assertEquals(501L, evento.userId());
        assertNotNull(evento.deletedAt());
    }

    @Test
    void noDebePublicarEventoCuandoUsuarioNoExiste() {

        when(usuarioRepository.eliminarPorId(501L))
                .thenReturn(false);

        assertThrows(
                UsuarioNoEncontradoException.class,
                () -> useCase.ejecutar(501L)
        );

        verify(usuarioRepository).eliminarPorId(501L);
        verifyNoInteractions(eventoPublisher);

        assertTrue(
                TransactionSynchronizationManager.getSynchronizations().isEmpty()
        );
    }
}
