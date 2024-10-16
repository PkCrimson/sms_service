# Implemented tech or tools
  - Spring Framework
  - Redis
  - RabbitMq
  - Aliyun SMS Service
# Config
## RabbitMq
  - Binding the authDirectQueue to directExchange through the routing key
```
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
```
