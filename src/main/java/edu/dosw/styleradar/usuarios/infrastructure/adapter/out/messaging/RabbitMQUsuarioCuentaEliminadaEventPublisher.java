package edu.dosw.styleradar.usuarios.infrastructure.adapter.out.messaging;

import edu.dosw.styleradar.usuarios.domain.event.UsuarioCuentaEliminadaEvent;
import edu.dosw.styleradar.usuarios.domain.port.out.UsuarioCuentaEliminadaEventPublisherPort;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQUsuarioCuentaEliminadaEventPublisher
        implements UsuarioCuentaEliminadaEventPublisherPort {

    private static final String EXCHANGE = "styleradar.exchange";
    private static final String ROUTING_KEY =
            "styleradar.usuarios.cuenta.eliminada";

    private final RabbitTemplate rabbitTemplate;

    public RabbitMQUsuarioCuentaEliminadaEventPublisher(
            RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void publicar(UsuarioCuentaEliminadaEvent evento) {
        rabbitTemplate.convertAndSend(EXCHANGE, ROUTING_KEY, evento);
    }
}

