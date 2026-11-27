package edu.dosw.styleradar.usuarios.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.dosw.styleradar.usuarios.domain.event.BusquedaSincronizadaEvent;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class RabbitMQConfigTest {

    @Test
    void serializaFechaEventoComoIso8601EnLugarDeTimestampNumerico() {
        RabbitMQConfig config = new RabbitMQConfig();
        Jackson2JsonMessageConverter converter = config.jacksonMessageConverter(new ObjectMapper());

        BusquedaSincronizadaEvent evento = BusquedaSincronizadaEvent.desde(
                10L, 25L, "Camisas negras", "camisa", null, "Negro", null,
                null, null, null, null, "CREADA");

        Message message = converter.toMessage(evento, new MessageProperties());
        String json = new String(message.getBody(), StandardCharsets.UTF_8);

        assertThat(json).contains("\"fechaEvento\":\"");
        assertThat(json).doesNotContainPattern("\"fechaEvento\":\\s*[0-9]");
    }
}
