package com.side.auth.service;

import com.side.auth.entities.SmsParam;
import com.side.auth.serviceImpl.MongoAuthServiceImpl;

/**
 * @see MongoAuthServiceImpl
 * **/
public interface AuthService {

    void sendSMS(SmsParam smsParam);

    void verifySmsCode(SmsParam smsParam);
}
