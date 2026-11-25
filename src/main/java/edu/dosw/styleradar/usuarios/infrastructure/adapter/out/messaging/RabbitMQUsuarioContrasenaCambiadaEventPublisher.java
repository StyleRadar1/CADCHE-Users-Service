package edu.dosw.styleradar.usuarios.infrastructure.adapter.out.messaging;

import edu.dosw.styleradar.usuarios.domain.event.UsuarioContrasenaCambiadaEvent;
import edu.dosw.styleradar.usuarios.domain.port.out.UsuarioContrasenaCambiadaEventPublisherPort;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQUsuarioContrasenaCambiadaEventPublisher
        implements UsuarioContrasenaCambiadaEventPublisherPort {

    private static final String EXCHANGE = "styleradar.exchange";
    private static final String ROUTING_KEY = "user.password.changed";

    private final RabbitTemplate rabbitTemplate;

    @SuppressFBWarnings(
            value = "EI_EXPOSE_REP2",
            justification = "RabbitTemplate es un bean administrado por Spring e inyectado por constructor.")
    public RabbitMQUsuarioContrasenaCambiadaEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void publicar(UsuarioContrasenaCambiadaEvent evento) {
        rabbitTemplate.convertAndSend(
                EXCHANGE,
                ROUTING_KEY,
                UsuarioContrasenaCambiadaMessage.desde(evento));
    }
}
