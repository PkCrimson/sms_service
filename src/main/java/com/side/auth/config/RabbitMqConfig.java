package com.side.auth.config;

import com.side.auth.entities.RabbitMqKeys;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitMqConfig {

    // Creating a direct exchange
    @Bean("directExchange")
    public Exchange DirectExchange() {
        return new CustomExchange(RabbitMqKeys.DIRECT_EXCHANGE,"direct",true,false);
    }

    // Creating a delay exchange
    @Bean("delayExchange")
    public Exchange DelayExchange() {
        Map<String, Object> args = new HashMap<>(1);
        args.put("x-delayed-type", "direct");
        return new CustomExchange(RabbitMqKeys.DELAY_EXCHANGE,"x-delayed-message",true,false,args);
    }


    // Creating a queue for Auth Service
    @Bean("authDirectRedisQueue")
    public Queue RedisAuthQueue() {
        return new Queue(RabbitMqKeys.AUTH_QUEUE_REDIS,true);
    }

    // Creating a queue for Auth Service
    @Bean("authDirectMongoQueue")
    public Queue MongoAuthQueue() {
        return new Queue(RabbitMqKeys.AUTH_QUEUE_MONGO,true);
    }



    // Creating a queue for Auth Service
    @Bean("authDelayMongoQueue")
    public Queue DelayAuthQueue() {
        return new Queue(RabbitMqKeys.AUTH_DELAY_QUEUE_MONGO,true);
    }

    // Binding the authDirectQueue to directExchange through the routing key
    @Bean("authDirectBindingRedis")
    public Binding bindingRedisDirectAuth(@Qualifier("authDirectRedisQueue")Queue queue, @Qualifier("directExchange") Exchange exchange) {
        return BindingBuilder
                .bind(queue)
                .to(exchange)
                .with(RabbitMqKeys.AUTH_REDIS_ROUTING_KEY)
                .noargs();
    }

    // Binding the authDirectQueue to directExchange through the routing key
    @Bean("authDirectBindingMongo")
    public Binding bindingMongoDirectAuth(@Qualifier("authDirectMongoQueue")Queue queue, @Qualifier("directExchange") Exchange exchange) {
        return BindingBuilder
                .bind(queue)
                .to(exchange)
                .with(RabbitMqKeys.AUTH_MONGO_ROUTING_KEY)
                .noargs();
    }

    // Binding the authDirectQueue to directExchange through the routing key
    @Bean("authDelayBinding")
    public Binding bindingDelayAuth(@Qualifier("authDelayMongoQueue")Queue queue, @Qualifier("delayExchange") Exchange exchange) {
        return BindingBuilder
                .bind(queue)
                .to(exchange)
                .with(RabbitMqKeys.AUTH_MONGO_ROUTING_KEY)
                .noargs();
    }
}
