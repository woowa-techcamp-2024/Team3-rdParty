package com.thirdparty.ticketing.global.waitingsystem.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thirdparty.ticketing.global.waitingsystem.redis.running.RedisRunningRoom;
import com.thirdparty.ticketing.global.waitingsystem.redis.waiting.RedisWaitingCounter;
import com.thirdparty.ticketing.global.waitingsystem.redis.waiting.RedisWaitingLine;
import com.thirdparty.ticketing.global.waitingsystem.redis.waiting.RedisWaitingManager;
import com.thirdparty.ticketing.global.waitingsystem.redis.waiting.RedisWaitingRoom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;

@TestConfiguration
public class TestRedisConfig {

    @Qualifier("lettuceRedisTemplate")
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Bean
    public RedisWaitingManager waitingManager(
            RedisWaitingLine waitingLine,
            RedisWaitingRoom waitingRoom,
            RedisWaitingCounter waitingCounter) {
        return new RedisWaitingManager(waitingRoom, waitingCounter, waitingLine);
    }

    @Bean
    public RedisWaitingRoom waitingRoom() {
        return new RedisWaitingRoom(redisTemplate, objectMapper);
    }

    @Bean
    public RedisWaitingLine waitingLine() {
        return new RedisWaitingLine(redisTemplate, objectMapper);
    }

    @Bean
    public RedisWaitingCounter waitingCounter() {
        return new RedisWaitingCounter(redisTemplate);
    }

    @Bean
    public RedisRunningRoom runningRoom() {
        return new RedisRunningRoom(redisTemplate);
    }
}
