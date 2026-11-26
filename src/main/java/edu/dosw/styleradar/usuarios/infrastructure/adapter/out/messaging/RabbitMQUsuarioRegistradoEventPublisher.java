package edu.dosw.styleradar.usuarios.infrastructure.adapter.out.messaging;

import edu.dosw.styleradar.usuarios.domain.event.UsuarioRegistradoEvent;
import edu.dosw.styleradar.usuarios.domain.port.out.UsuarioRegistradoEventPublisherPort;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.amqp.core.ReturnedMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQUsuarioRegistradoEventPublisher
        implements UsuarioRegistradoEventPublisherPort {

    private static final Logger LOGGER = LoggerFactory.getLogger(
            RabbitMQUsuarioRegistradoEventPublisher.class);

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
        CorrelationData correlationData = new CorrelationData(evento.eventId());
        rabbitTemplate.convertAndSend(
                EXCHANGE,
                ROUTING_KEY,
                UsuarioRegistradoMessage.desde(evento),
                correlationData);

        correlationData.getFuture().whenComplete((confirm, error) -> {
            if (error != null) {
                LOGGER.error(
                        "Falló la confirmación del evento de registro en RabbitMQ: "
                                + "eventId={}, exchange={}, routingKey={}",
                        evento.eventId(), EXCHANGE, ROUTING_KEY, error);
                return;
            }

            if (!confirm.isAck()) {
                LOGGER.error(
                        "RabbitMQ rechazó el evento de registro: eventId={}, "
                                + "exchange={}, routingKey={}, reason={}",
                        evento.eventId(), EXCHANGE, ROUTING_KEY, confirm.getReason());
                return;
            }

            ReturnedMessage returned = correlationData.getReturned();
            if (returned != null) {
                LOGGER.error(
                        "RabbitMQ confirmó el evento de registro pero no encontró "
                                + "una cola vinculada: eventId={}, exchange={}, "
                                + "routingKey={}, replyCode={}",
                        evento.eventId(), EXCHANGE, ROUTING_KEY, returned.getReplyCode());
                return;
            }

            LOGGER.info(
                    "Evento de registro confirmado por RabbitMQ y enrutado: "
                            + "eventId={}, exchange={}, routingKey={}",
                    evento.eventId(), EXCHANGE, ROUTING_KEY);
        });
    }
}
