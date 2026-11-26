package edu.dosw.styleradar.usuarios.infrastructure.adapter.out.messaging;

import edu.dosw.styleradar.usuarios.domain.event.UsuarioRegistradoEvent;
import edu.dosw.styleradar.usuarios.domain.port.out.UsuarioRegistradoEventPublisherPort;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQUsuarioRegistradoEventPublisher
        implements UsuarioRegistradoEventPublisherPort {

    private static final String EXCHANGE = "styleradar.exchange";
    private static final String ROUTING_KEY =
            "styleradar.usuarios.usuario.registrado";

    private final RabbitTemplate rabbitTemplate;

    @SuppressFBWarnings(
            value = "EI_EXPOSE_REP2",
            justification = "RabbitTemplate es un bean administrado por Spring e inyectado por constructor."
    )
    public RabbitMQUsuarioRegistradoEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void publicar(UsuarioRegistradoEvent evento) {
        rabbitTemplate.convertAndSend(
                EXCHANGE,
                ROUTING_KEY,
                UsuarioRegistradoMessage.desde(evento));
    }
}
