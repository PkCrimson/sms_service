package com.side.auth.entities;

public enum SmsTemplate {
    EN("Subject","You are using the SMS code verification. Please do not disclose it to others. Verification code: #{code}. Expired at 2 minutes."),
    ZH_HK("主旨","您正在使用簡訊驗證碼登入功能，轉送可能導致帳號被盜，請勿洩漏給他人，驗證碼：#{code}，2分鐘內有效。"),
    ZH_CN("主旨","您正在使用短信验证码登录功能，转发可能导致账号被盗，请勿泄露给他人，验证码：#{code}，2分钟内有效。"),
    ;

    private final String subject;

    private final String text;

    SmsTemplate(String subject, String text){
        this.subject = subject;
        this.text = text;
    }

    public String getText(){
        return this.text;
    }

    public String getSubject(){
        return this.subject;
    }
}
