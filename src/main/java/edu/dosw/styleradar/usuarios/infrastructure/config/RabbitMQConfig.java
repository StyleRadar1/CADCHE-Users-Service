package edu.dosw.styleradar.usuarios.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class RabbitMQConfig {

    @Bean
    @Profile("!qa")
    TopicExchange styleradarExchange() {
        return new TopicExchange("styleradar.exchange", true, false);
    }

    @Bean
    Jackson2JsonMessageConverter jacksonMessageConverter(ObjectMapper objectMapper) {
        ObjectMapper rabbitMapper = objectMapper.copy();
        rabbitMapper.registerModule(new JavaTimeModule());
        rabbitMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return new Jackson2JsonMessageConverter(rabbitMapper);
    }

    @Bean
    RabbitTemplate rabbitTemplate(
            ConnectionFactory connectionFactory,
            Jackson2JsonMessageConverter converter) {

        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(converter);
        template.setMandatory(true);

        return template;
    }
}
