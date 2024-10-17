package com.side.auth.controller;

import com.side.auth.entities.SmsParam;
import com.side.auth.service.AuthService;
import com.side.auth.utils.LoggerUtil;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Level;
import java.util.logging.Logger;


@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger logger = LoggerUtil.logger;

    private final AuthService redisAuthService;

    private final AuthService mongoAuthService;

    public AuthController(@Qualifier("RedisAuth") AuthService redisAuthService,
                          @Qualifier("MongoAuth") AuthService mongoAuthService) {
        this.redisAuthService = redisAuthService;
        this.mongoAuthService = mongoAuthService;
    }

    @PostMapping("/sms/send/{db}")
    public ResponseEntity<String> sendSMSByCountryCodeAndPhoneNo(@PathVariable String db,
                                                                 @RequestBody SmsParam smsParam) {
        try {
            if(db.equals("redis")){
                redisAuthService.sendSMS(smsParam);
            }
            else if(db.equals("mongo")){
                mongoAuthService.sendSMS(smsParam);
            }
            return ResponseEntity.ok()
                    .body("OK");
        }
        catch (Exception e){
            logger.log(Level.SEVERE, e.getMessage(), e);
            return ResponseEntity.ok()
                    .body("Send SMS failed");
        }

    }

    @PostMapping("/sms/verify/{db}")
    public ResponseEntity<String> verifySMS(@PathVariable String db,
                                            @RequestBody SmsParam smsParam) {
        try {
            if(db.equals("redis")){
                redisAuthService.verifySmsCode(smsParam);
            }else if(db.equals("mongo")){
                mongoAuthService.verifySmsCode(smsParam);
            }
            return ResponseEntity.ok()
                    .body("OK");
        }
        catch (NullPointerException e){
            logger.log(Level.SEVERE, e.getMessage(), e);
            return ResponseEntity.ok()
                    .body("Verification Expired");
        }
        catch (IllegalArgumentException e){
            logger.log(Level.SEVERE, e.getMessage(), e);
            return ResponseEntity.ok()
                    .body("Verification Failed");
        }
    }


}
