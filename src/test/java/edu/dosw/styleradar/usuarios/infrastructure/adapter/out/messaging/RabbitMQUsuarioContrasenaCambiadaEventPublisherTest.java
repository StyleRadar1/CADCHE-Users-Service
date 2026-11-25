package edu.dosw.styleradar.usuarios.infrastructure.adapter.out.messaging;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import edu.dosw.styleradar.usuarios.domain.event.UsuarioContrasenaCambiadaEvent;

class RabbitMQUsuarioContrasenaCambiadaEventPublisherTest {

    @Test
    void publicaEventoDeCambioDeContrasenaConRoutingKeyAcordada() {
        RabbitTemplate rabbitTemplate = mock(RabbitTemplate.class);
        RabbitMQUsuarioContrasenaCambiadaEventPublisher publisher =
                new RabbitMQUsuarioContrasenaCambiadaEventPublisher(rabbitTemplate);
        Instant changedAt = Instant.parse("2026-11-25T16:30:00Z");
        UsuarioContrasenaCambiadaEvent event =
                new UsuarioContrasenaCambiadaEvent("event-123", 42L, changedAt);

        publisher.publicar(event);

        verify(rabbitTemplate).convertAndSend(
                "styleradar.exchange",
                "user.password.changed",
                new UsuarioContrasenaCambiadaMessage(
                        "event-123", "user.password.changed", 42L, changedAt));
    }
}
