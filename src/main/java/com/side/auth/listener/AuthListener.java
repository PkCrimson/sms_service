package com.side.auth.listener;

import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.side.auth.dao.SmsDao;
import com.side.auth.documents.SmsDocument;
import com.side.auth.entities.SmsTemplate;
import com.side.auth.entities.RabbitMqKeys;
import com.side.auth.entities.SmsParam;
import com.side.auth.utils.AliyunSMSUtil;
import com.side.auth.utils.LoggerUtil;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AuthListener {

    private static final Logger logger = LoggerUtil.logger;

    private final RedisTemplate<String,Object> redisTemplate;

    private final AmqpTemplate amqpTemplate;

    private final SmsDao smsDao;

    public AuthListener(@Qualifier("configRedisTemplate") RedisTemplate<String,Object> redisTemplate,
                        AmqpTemplate amqpTemplate,
                        SmsDao smsDao) {
        this.redisTemplate = redisTemplate;
        this.amqpTemplate = amqpTemplate;
        this.smsDao = smsDao;
    }

    /**
     * @desc Sending SMS by receiving message from AUTH queue
     * @see SmsTemplate
     * @see com.side.auth.serviceImpl.AuthServiceImpl
     * **/
    @RabbitListener(queues = RabbitMqKeys.AUTH_QUEUE)
    private void smsSenderImplRedis(Channel channel, Message message){
        try {
            final String json = new String(message.getBody(), StandardCharsets.UTF_8);

            final SmsParam smsParam = new Gson().fromJson(json, SmsParam.class);
            smsParam.setCode(codeGenerator());

            final String key = "sms-"+smsParam.getCountryCode()+smsParam.getPhoneNumber();

            redisTemplate.boundValueOps(key).set(smsParam.getCode(), 120, TimeUnit.SECONDS);

            AliyunSMSUtil.smsSender(smsParam);

            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);

        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }

    }

    /**
     * @desc Sending SMS by receiving message from AUTH queue
     * @see SmsTemplate
     * @see com.side.auth.serviceImpl.AuthServiceImpl
     * **/
    @RabbitListener(queues = RabbitMqKeys.AUTH_QUEUE)
    private void smsSenderImplMongo(Channel channel, Message message) throws IOException {
        try {
            final String json = new String(message.getBody(), StandardCharsets.UTF_8);

            final SmsParam smsParam = new Gson().fromJson(json, SmsParam.class);
            smsParam.setCode(codeGenerator());

            // Create SmsDocument to insert into Mongo DB
            final SmsDocument smsDocument = new SmsDocument(smsParam);

            final LocalDateTime now = LocalDateTime.now();

            smsDocument.setCreateAt(now);

            // inserting into Mongo DB
            smsDao.addOne(smsDocument);

            // Send sms through AliYun Service
            AliyunSMSUtil.smsSender(smsParam);

            // Send delay message to queue for expiration of verification
            MessageProperties properties = new MessageProperties();
            properties.setHeader("x-delay", Duration.ofSeconds(120).toMillis());
            amqpTemplate.convertAndSend(
                    RabbitMqKeys.AUTH_DELAY_QUEUE,
                    RabbitMqKeys.AUTH_ROUTING_KEY,
                    MessageBuilder
                            .withBody(smsDocument.getId().getBytes())
                            .andProperties(properties).build()
            );
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    @RabbitListener(queues = RabbitMqKeys.AUTH_DELAY_QUEUE)
    private void deleteSmsDocument(Channel channel, Message message) throws IOException {
        try {
            final String id = new String(message.getBody(), StandardCharsets.UTF_8);

            if(smsDao.deleteById(id)){
               logger.log(Level.INFO, "Delete smsDocument successfully");
            }
            logger.log(Level.INFO,"Delete smsDocument failed");

        }catch (Exception e){
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }



    private static String codeGenerator() {
        StringBuilder code = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 4; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }
}
