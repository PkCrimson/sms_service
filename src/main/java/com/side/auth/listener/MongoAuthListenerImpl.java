package com.side.auth.listener;

import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.side.auth.dao.SmsDao;
import com.side.auth.documents.SmsDocument;
import com.side.auth.entities.RabbitMqKeys;
import com.side.auth.entities.SmsParam;
import com.side.auth.entities.SmsTemplate;
import com.side.auth.serviceImpl.MongoAuthServiceImpl;
import com.side.auth.utils.AliyunSMSUtil;
import com.side.auth.utils.CodeGenerator;
import com.side.auth.utils.LoggerUtil;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MongoAuthListenerImpl implements AuthListener {

    private static final Logger logger = LoggerUtil.logger;

    private final AmqpTemplate amqpTemplate;

    private final SmsDao smsDao;

    public MongoAuthListenerImpl(AmqpTemplate amqpTemplate,
                                 SmsDao smsDao) {
        this.amqpTemplate = amqpTemplate;
        this.smsDao = smsDao;
    }

    /**
     * @desc Sending SMS by receiving message from AUTH queue
     * @see SmsTemplate
     * @see MongoAuthServiceImpl
     * **/
    @Override
    @RabbitListener(queues = RabbitMqKeys.AUTH_QUEUE_MONGO)
    public void smsSender(Channel channel, Message message) throws IOException {
        try {
            final String json = new String(message.getBody(), StandardCharsets.UTF_8);

            final SmsParam smsParam = new Gson().fromJson(json, SmsParam.class);
            smsParam.setCode(CodeGenerator.GenerateSmsCode());

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
                    RabbitMqKeys.AUTH_DELAY_QUEUE_MONGO,
                    RabbitMqKeys.AUTH_MONGO_ROUTING_KEY,
                    MessageBuilder
                            .withBody(smsDocument.getId().getBytes())
                            .andProperties(properties).build()
            );
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    @Override
    @RabbitListener(queues = RabbitMqKeys.AUTH_DELAY_QUEUE_MONGO)
    public void deleteVerifier(Channel channel, Message message) throws IOException {
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
}
