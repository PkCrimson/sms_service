package com.side.auth.controller;

import com.side.auth.entities.SmsParam;
import com.side.auth.service.AuthService;
import com.side.auth.utils.LoggerUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.logging.Level;
import java.util.logging.Logger;


@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger logger = LoggerUtil.logger;

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/sms/send")
    public ResponseEntity<String> sendSMSByCountryCodeAndPhoneNo(@RequestBody SmsParam smsParam) {
        try {
            authService.sendSMS(smsParam);
            return ResponseEntity.ok()
                    .body("OK");
        }
        catch (Exception e){
            logger.log(Level.SEVERE, e.getMessage(), e);
            return ResponseEntity.ok()
                    .body("Send SMS failed");
        }

    }

    @PostMapping("/sms/verify")
    public ResponseEntity<String> verifySMS(@RequestBody SmsParam smsParam) {
        try {
            authService.verifySmsCode(smsParam);
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
