package com.side.auth.entities;


public class RabbitMqKeys {

    public static final String DIRECT_EXCHANGE = "rabbitmq.exchange.direct";

    public static final String DELAY_EXCHANGE = "rabbitmq.exchange.delay";

    public static final String AUTH_QUEUE= "rabbitmq.queue.auth";

    public static final String AUTH_DELAY_QUEUE= "rabbitmq.queue.auth.delay";

    public static final String AUTH_ROUTING_KEY= "rabbitmq.routing.key.auth";
}
