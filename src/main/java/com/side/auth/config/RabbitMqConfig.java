package com.side.auth.config;

import com.side.auth.entities.RabbitMqKeys;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    // Creating a direct exchange
    @Bean("directExchange")
    public Exchange DirectExchange() {
        return new CustomExchange(RabbitMqKeys.DIRECT_EXCHANGE,"direct",true,false);
    }

    // Creating a queue for Auth Service
    @Bean("authDirectQueue")
    public Queue AuthQueue() {
        return new Queue(RabbitMqKeys.AUTH_QUEUE,true);
    }

    // Binding the authDirectQueue to directExchange through the routing key
    @Bean("authDirectBinding")
    public Binding bindingDirectAuth(@Qualifier("authDirectQueue")Queue queue, @Qualifier("directExchange") Exchange exchange) {
        return BindingBuilder
                .bind(queue)
                .to(exchange)
                .with(RabbitMqKeys.AUTH_ROUTING_KEY)
                .noargs();
    }
}
