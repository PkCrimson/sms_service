package com.side.auth.dao;

import com.side.auth.documents.SmsDocument;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Objects;

@Repository
public class SmsDao {

    private final MongoTemplate mongoTemplate;

    public SmsDao(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public void addOne(SmsDocument smsDocument){
        mongoTemplate.save(smsDocument);
    }

    public Boolean deleteById(String id){
        mongoTemplate.remove(Objects.requireNonNull(mongoTemplate.findById(id, SmsDocument.class)));
        return mongoTemplate.findById(id, SmsDocument.class) == null;
    }

    public SmsDocument findByCountryCodeAndPhone(String countryCode, String phone){
       Query query = new Query();
       query.addCriteria(Criteria.where("country_code").is(countryCode)).addCriteria(Criteria.where("phone").is(phone));

       return mongoTemplate.findOne(query, SmsDocument.class);
    }
}
