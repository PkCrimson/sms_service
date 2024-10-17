package com.side.auth.entities;


public class RabbitMqKeys {

    public static final String DIRECT_EXCHANGE = "rabbitmq.exchange.direct";

    public static final String DELAY_EXCHANGE = "rabbitmq.exchange.delay";

    public static final String AUTH_QUEUE_REDIS= "rabbitmq.queue.auth.redis";

    public static final String AUTH_QUEUE_MONGO= "rabbitmq.queue.auth.mongo";

    public static final String AUTH_DELAY_QUEUE_MONGO= "rabbitmq.queue.auth.delay.mongo";

    public static final String AUTH_REDIS_ROUTING_KEY= "rabbitmq.routing.key.auth.redis";

    public static final String AUTH_MONGO_ROUTING_KEY= "rabbitmq.routing.key.auth.mongo";
}
