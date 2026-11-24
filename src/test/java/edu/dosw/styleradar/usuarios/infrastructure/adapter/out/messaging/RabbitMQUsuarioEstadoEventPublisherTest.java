package edu.dosw.styleradar.usuarios.infrastructure.adapter.out.messaging;

import edu.dosw.styleradar.usuarios.domain.event.UsuarioEstadoCambiadoEvent;
import edu.dosw.styleradar.usuarios.domain.model.EstadoCuenta;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.time.Instant;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class RabbitMQUsuarioEstadoEventPublisherTest {

    @Test
    void debePublicarEventoConExchangeYRoutingKeyCorrectos() {

        RabbitTemplate rabbitTemplate = mock(RabbitTemplate.class);

        RabbitMQUsuarioEstadoEventPublisher publisher =
                new RabbitMQUsuarioEstadoEventPublisher(rabbitTemplate);

        UsuarioEstadoCambiadoEvent evento =
                new UsuarioEstadoCambiadoEvent(
                        "evento-prueba",
                        144L,
                        "usuario@styleradar.com",
                        EstadoCuenta.ACTIVA,
                        EstadoCuenta.INACTIVA,
                        501L,
                        "Prueba de cambio de estado",
                        Instant.parse("2026-11-22T15:00:00Z")
                );

        publisher.publicar(evento);

        verify(rabbitTemplate).convertAndSend(
                "styleradar.exchange",
                "user.status.changed",
                new UsuarioEstadoCambiadoMessage(
                        "evento-prueba", 144L, "ACTIVE", "INACTIVE", 501L,
                        "Prueba de cambio de estado",
                        Instant.parse("2026-11-22T15:00:00Z"))
        );
    }
}

