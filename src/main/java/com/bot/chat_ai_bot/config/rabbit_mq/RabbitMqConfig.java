package com.bot.chat_ai_bot.config.rabbit_mq;

import com.bot.chat_ai_bot.config.rabbit_mq.dto.RabbitMqDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
@RequiredArgsConstructor
public class RabbitMqConfig {
    private final RabbitMqDto rabbitMqDto;

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(rabbitMqDto.exchange());
    }

    @Bean
    public Queue queue() {
        return new Queue(rabbitMqDto.queueMood());
    }

    @Bean
    public Binding binding(Queue queue, DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(rabbitMqDto.key());
    }
}
