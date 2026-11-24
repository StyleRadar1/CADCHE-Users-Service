package edu.dosw.styleradar.usuarios.infrastructure.adapter.out.messaging;

import edu.dosw.styleradar.usuarios.domain.event.BusquedaSincronizadaEvent;
import edu.dosw.styleradar.usuarios.domain.port.out.BusquedaSincronizadaEventPublisherPort;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQBusquedaSincronizadaEventPublisher
        implements BusquedaSincronizadaEventPublisherPort {

    private static final String EXCHANGE = "styleradar.exchange";
    private static final String ROUTING_KEY =
            "styleradar.usuarios.busqueda.sincronizada";

    private final RabbitTemplate rabbitTemplate;

    @SuppressFBWarnings(
            value = "EI_EXPOSE_REP2",
            justification = "RabbitTemplate es una dependencia administrada por Spring e inyectada por constructor."
    )
    public RabbitMQBusquedaSincronizadaEventPublisher(
            RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void publicar(BusquedaSincronizadaEvent evento) {
        rabbitTemplate.convertAndSend(EXCHANGE, ROUTING_KEY, evento);
    }
}
