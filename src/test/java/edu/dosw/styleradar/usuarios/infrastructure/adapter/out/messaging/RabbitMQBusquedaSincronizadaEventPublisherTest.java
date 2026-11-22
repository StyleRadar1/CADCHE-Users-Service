package edu.dosw.styleradar.usuarios.infrastructure.adapter.out.messaging;

import edu.dosw.styleradar.usuarios.domain.event.BusquedaSincronizadaEvent;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class RabbitMQBusquedaSincronizadaEventPublisherTest {

    @Test
    void debePublicarEventoConExchangeYRoutingKeyCorrectos() {

        RabbitTemplate rabbitTemplate = mock(RabbitTemplate.class);

        RabbitMQBusquedaSincronizadaEventPublisher publisher =
                new RabbitMQBusquedaSincronizadaEventPublisher(
                        rabbitTemplate
                );

        BusquedaSincronizadaEvent evento =
                new BusquedaSincronizadaEvent(
                        "evento-prueba",
                        10L,
                        144L,
                        "Chaquetas urbanas",
                        "chaqueta urbana",
                        "SUPERIOR",
                        "Azul",
                        "M",
                        50000.0,
                        150000.0,
                        "Marca prueba",
                        "URBANO",
                        null,
                        "ACTUALIZADA",
                        java.time.Instant.parse("2026-11-22T15:00:00Z")
                );

        publisher.publicar(evento);

        verify(rabbitTemplate).convertAndSend(
                "styleradar.exchange",
                "styleradar.usuarios.busqueda.sincronizada",
                evento
        );
    }
}
