package com.side.auth.documents;

import com.side.auth.entities.SmsParam;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.LocalDateTime;

@Document("t_sms")
public class SmsDocument {

    @MongoId
    private String id;

    private LocalDateTime createAt;

    @Field("country_code")
    private String countryCode;

    private String phone;

    private String code;

    public SmsDocument() {
    }

    public SmsDocument(SmsParam smsParam) {
        this.countryCode = smsParam.getCountryCode();
        this.phone = smsParam.getPhoneNumber();
        this.code = smsParam.getCode();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
