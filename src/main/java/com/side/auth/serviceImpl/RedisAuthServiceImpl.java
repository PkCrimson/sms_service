package com.side.auth.serviceImpl;

import com.google.gson.Gson;
import com.side.auth.entities.RabbitMqKeys;
import com.side.auth.entities.SmsParam;
import com.side.auth.listener.RedisAuthListenerImpl;
import com.side.auth.service.AuthService;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service("RedisAuth")
public class RedisAuthServiceImpl implements AuthService {

    private final AmqpTemplate amqpTemplate;

    private final RedisTemplate<String, Object> redisTemplate;

    public RedisAuthServiceImpl(AmqpTemplate amqpTemplate,
                           @Qualifier("configRedisTemplate") RedisTemplate<String, Object> redisTemplate) {
        this.amqpTemplate = amqpTemplate;
        this.redisTemplate = redisTemplate;
    }

    /**
     * @param smsParam The country code and phone number to which the SMS will be sent, and the language for determine the SMS template.
     * @desc SMS sending service calling from Controller, messaging to AUTH queue for asynchronous process
     * @see RedisAuthListenerImpl
     **/
    @Override
    public void sendSMS(SmsParam smsParam) {
        final String json = new Gson().toJson(smsParam);

        amqpTemplate.convertAndSend(
                RabbitMqKeys.DIRECT_EXCHANGE,
                RabbitMqKeys.AUTH_REDIS_ROUTING_KEY,
                json.getBytes());
    }

    /**
     * @param smsParam The country code and phone number to be queried by forming as redis key , and the code for matching value of key.
     * @desc SMS verification service calling from Controller
     **/

    @Override
    public void verifySmsCode(SmsParam smsParam) {
        final String key = "sms-"+smsParam.getCountryCode()+smsParam.getPhoneNumber();

        if(Boolean.FALSE.equals(redisTemplate.hasKey(key))){
            throw new NullPointerException("Error: SMS key not exists");
        }
        final String value = (String) redisTemplate.boundValueOps(key).get();

        if(!Objects.equals(value, smsParam.getCode())){
            throw new IllegalArgumentException("Error: Sms code not match");
        }
    }
}
