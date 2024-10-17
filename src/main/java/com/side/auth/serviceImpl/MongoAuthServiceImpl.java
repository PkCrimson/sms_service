package com.side.auth.serviceImpl;

import com.side.auth.dao.SmsDao;
import com.side.auth.documents.SmsDocument;
import com.side.auth.entities.RabbitMqKeys;
import com.side.auth.entities.SmsParam;
import com.side.auth.listener.MongoAuthListenerImpl;
import com.side.auth.listener.RedisAuthListenerImpl;
import com.side.auth.service.AuthService;
import org.springframework.amqp.core.AmqpTemplate;

import org.springframework.stereotype.Service;
import com.google.gson.Gson;

import java.util.Objects;
@Service("MongoAuth")
public class MongoAuthServiceImpl implements AuthService {

    private final AmqpTemplate amqpTemplate;
    private final SmsDao smsDao;

    public MongoAuthServiceImpl(AmqpTemplate amqpTemplate,
                                SmsDao smsDao) {
        this.amqpTemplate = amqpTemplate;
        this.smsDao = smsDao;
    }

    /**
     * @param smsParam The country code and phone number to which the SMS will be sent, and the language for determine the SMS template.
     * @desc SMS sending service calling from Controller, messaging to AUTH queue for asynchronous process
     * @see MongoAuthListenerImpl
     **/
    @Override
    public void sendSMS(SmsParam smsParam) {
        final String json = new Gson().toJson(smsParam);

        amqpTemplate.convertAndSend(
                RabbitMqKeys.DIRECT_EXCHANGE,
                RabbitMqKeys.AUTH_MONGO_ROUTING_KEY,
                json.getBytes());
    }

    /**
     * @param smsParam The country code and phone number to be condition queried in Mongo , and the code for matching value of key.
     * @desc SMS verification service calling from Controller
     **/
    @Override
    public void verifySmsCode(SmsParam smsParam) {

        SmsDocument smsDocument = smsDao.findByCountryCodeAndPhone(smsParam.getCountryCode(),smsParam.getPhoneNumber());
        if(Objects.isNull(smsDocument)){
            throw new NullPointerException("Error: Sms document not exists");
        }

        if(!smsDocument.getCode().equals(smsParam.getCode())){
            throw new IllegalArgumentException("Error: Sms code not match");
        }
    }


}
