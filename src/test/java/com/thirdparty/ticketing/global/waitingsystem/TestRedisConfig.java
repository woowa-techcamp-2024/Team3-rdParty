package com.thirdparty.ticketing.global.waitingsystem;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.thirdparty.ticketing.global.waitingsystem.running.RedisRunningRoom;

@TestConfiguration
public class TestRedisConfig {

    @Qualifier("lettuceRedisTemplate")
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Bean
    public RedisWaitingRoom waitingRoom() {
        return new RedisWaitingRoom(redisTemplate);
    }

    @Bean
    public RedisRunningRoom runningRoom() {
        return new RedisRunningRoom(redisTemplate);
    }
}
