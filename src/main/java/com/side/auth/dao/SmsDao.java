package com.side.auth.dao;

import com.side.auth.documents.SmsDocument;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import java.util.Objects;

@Repository
public class SmsDao {

    private final MongoTemplate mongoTemplate;

    public SmsDao(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public SmsDocument addOne(SmsDocument smsDocument){
       return mongoTemplate.save(smsDocument);
    }

    public Boolean deleteById(String id){
        mongoTemplate.remove(Objects.requireNonNull(mongoTemplate.findById(id, SmsDocument.class)));
        return mongoTemplate.findById(id, SmsDocument.class) == null;
    }
}
