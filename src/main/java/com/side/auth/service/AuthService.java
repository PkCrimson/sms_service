package com.side.auth.service;

import com.side.auth.entities.SmsParam;
/**
 * @see com.side.auth.serviceImpl.AuthServiceImpl
 * **/
public interface AuthService {

    void sendSMS(SmsParam smsParam);

    void verifySmsCode(SmsParam smsParam);
}
