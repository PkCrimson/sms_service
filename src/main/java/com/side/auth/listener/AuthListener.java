package com.side.auth.listener;

import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.side.auth.entities.SmsTemplate;
import com.side.auth.entities.RabbitMqKeys;
import com.side.auth.entities.SmsParam;
import com.side.auth.utils.AliyunSMSUtil;
import com.side.auth.utils.LoggerUtil;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AuthListener {

    private static final Logger logger = LoggerUtil.logger;

    private final RedisTemplate<String,Object> redisTemplate;

    public AuthListener(@Qualifier("configRedisTemplate") RedisTemplate<String,Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * @desc Sending SMS by receiving message from AUTH queue
     * @see SmsTemplate
     * @see com.side.auth.serviceImpl.AuthServiceImpl
     * **/
    @RabbitListener(queues = RabbitMqKeys.AUTH_QUEUE)
    private void smsSender(Channel channel, Message message){
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

    private static String codeGenerator() {
        StringBuilder code = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 4; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }
}
