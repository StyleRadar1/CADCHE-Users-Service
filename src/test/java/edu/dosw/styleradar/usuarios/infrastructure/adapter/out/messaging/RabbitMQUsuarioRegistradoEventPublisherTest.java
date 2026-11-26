package edu.dosw.styleradar.usuarios.infrastructure.adapter.out.messaging;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import edu.dosw.styleradar.usuarios.domain.event.UsuarioRegistradoEvent;
import edu.dosw.styleradar.usuarios.domain.model.Rol;

class RabbitMQUsuarioRegistradoEventPublisherTest {

    @Test
    void publicaContratoDeRegistroSinDatosPersonales() {
        RabbitTemplate rabbitTemplate = mock(RabbitTemplate.class);
        RabbitMQUsuarioRegistradoEventPublisher publisher =
                new RabbitMQUsuarioRegistradoEventPublisher(rabbitTemplate);
        UsuarioRegistradoEvent event = new UsuarioRegistradoEvent(
                "550e8400-e29b-41d4-a716-446655440020",
                Instant.parse("2026-11-24T16:30:00Z"),
                Rol.COMPRADOR);

        publisher.publicar(event);

        ArgumentCaptor<UsuarioRegistradoMessage> messageCaptor =
                ArgumentCaptor.forClass(UsuarioRegistradoMessage.class);
        ArgumentCaptor<CorrelationData> correlationCaptor =
                ArgumentCaptor.forClass(CorrelationData.class);
        verify(rabbitTemplate).convertAndSend(
                eq("styleradar.exchange"),
                eq("styleradar.usuarios.usuario.registrado"),
                messageCaptor.capture(),
                correlationCaptor.capture());

        UsuarioRegistradoMessage message = messageCaptor.getValue();
        assertEquals(event.eventId(), correlationCaptor.getValue().getId());
        assertEquals(event.eventId(), message.eventId());
        assertEquals("styleradar.usuarios.usuario.registrado", message.eventType());
        assertEquals("2026-11-24T16:30:00Z", message.occurredAt());
        assertEquals("BUYER", message.data().rol());
    }
}
