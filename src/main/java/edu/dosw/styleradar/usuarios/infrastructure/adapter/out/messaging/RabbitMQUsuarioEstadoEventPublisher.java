package edu.dosw.styleradar.usuarios.infrastructure.adapter.out.messaging;

import edu.dosw.styleradar.usuarios.domain.event.UsuarioEstadoCambiadoEvent;
import edu.dosw.styleradar.usuarios.domain.port.out.UsuarioEstadoEventPublisherPort;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQUsuarioEstadoEventPublisher
        implements UsuarioEstadoEventPublisherPort {

    private static final String EXCHANGE = "styleradar.usuarios";
    private static final String ROUTING_KEY = "usuario.estado.cambiado";

    private final RabbitTemplate rabbitTemplate;

    public RabbitMQUsuarioEstadoEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void publicar(UsuarioEstadoCambiadoEvent evento) {
        rabbitTemplate.convertAndSend(EXCHANGE, ROUTING_KEY, evento);
    }
}