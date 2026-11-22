package edu.dosw.styleradar.usuarios.infrastructure.adapter.out.messaging;

import edu.dosw.styleradar.usuarios.domain.event.UsuarioCuentaEliminadaEvent;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.time.Instant;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class RabbitMQUsuarioCuentaEliminadaEventPublisherTest {

    @Test
    void debePublicarEventoConExchangeYRoutingKeyCorrectos() {

        RabbitTemplate rabbitTemplate = mock(RabbitTemplate.class);

        RabbitMQUsuarioCuentaEliminadaEventPublisher publisher =
                new RabbitMQUsuarioCuentaEliminadaEventPublisher(
                        rabbitTemplate
                );

        UsuarioCuentaEliminadaEvent evento =
                new UsuarioCuentaEliminadaEvent(
                        501L,
                        Instant.parse("2026-11-21T22:00:00Z")
                );

        publisher.publicar(evento);

        verify(rabbitTemplate).convertAndSend(
                "styleradar.exchange",
                "styleradar.usuarios.cuenta.eliminada",
                evento
        );
    }
}

