package com.side.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

@Configuration
public class RedisTemplateConfig {

    @Bean("configRedisTemplate")
    @SuppressWarnings("all")
    public RedisTemplate<String, Object> redisTemplate(LettuceConnectionFactory lettuceConnectionFactory) {

        RedisTemplate<String,Object> redisTemplate = new RedisTemplate<>();

        redisTemplate.setConnectionFactory(lettuceConnectionFactory);

        // Set the key serialization method string, RedisSerializer.string() is equivalent to new StringRedisSerializer()
        redisTemplate.setKeySerializer(RedisSerializer.string());

        // Set the value serialization method json,
        // use GenericJackson2JsonRedisSerializer to replace the default serialization,
        // RedisSerializer.json() is equivalent to new GenericJackson2JsonRedisSerializer()
        redisTemplate.setValueSerializer(RedisSerializer.json());

        // Set the serialization method of hash key
        redisTemplate.setHashKeySerializer(RedisSerializer.string());

        // Set the serialization method of hash value
        redisTemplate.setHashValueSerializer(RedisSerializer.json());

        // Make the configuration effective
        redisTemplate.afterPropertiesSet();

        return redisTemplate;
    }
}
