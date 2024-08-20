package com.thirdparty.ticketing.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class LettuceConfig {

    @Bean
    public LettuceConnectionFactory lettuceConnectionFactory(
            @Value("${spring.data.redis.port}") int port,
            @Value("${spring.data.redis.host}") String host) {
        return new LettuceConnectionFactory(host, port);
    }

    @Bean
    public StringRedisTemplate lettuceRedisTemplate(
            LettuceConnectionFactory lettuceConnectionFactory) {
        StringRedisTemplate redisTemplate = new StringRedisTemplate();
        redisTemplate.setConnectionFactory(lettuceConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new StringRedisSerializer());
        return redisTemplate;
    }
}
