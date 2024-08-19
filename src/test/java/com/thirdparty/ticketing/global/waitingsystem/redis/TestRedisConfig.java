package com.thirdparty.ticketing.global.waitingsystem.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thirdparty.ticketing.domain.common.EventPublisher;
import com.thirdparty.ticketing.domain.waitingsystem.WaitingSystem;
import com.thirdparty.ticketing.global.waitingsystem.redis.running.RedisRunningCounter;
import com.thirdparty.ticketing.global.waitingsystem.redis.running.RedisRunningManager;
import com.thirdparty.ticketing.global.waitingsystem.redis.running.RedisRunningRoom;
import com.thirdparty.ticketing.global.waitingsystem.redis.waiting.RedisWaitingCounter;
import com.thirdparty.ticketing.global.waitingsystem.redis.waiting.RedisWaitingLine;
import com.thirdparty.ticketing.global.waitingsystem.redis.waiting.RedisWaitingManager;
import com.thirdparty.ticketing.global.waitingsystem.redis.waiting.RedisWaitingRoom;

@TestConfiguration
public class TestRedisConfig {

    @Autowired private StringRedisTemplate redisTemplate;

    @Autowired private ObjectMapper objectMapper;

    @Bean
    public WaitingSystem waitingSystem(
            RedisWaitingManager waitingManager,
            RedisRunningManager runningManager,
            EventPublisher eventPublisher) {
        return new WaitingSystem(waitingManager, runningManager, eventPublisher);
    }

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
    public RedisRunningManager runningManager(
            RedisRunningCounter runningCounter, RedisRunningRoom runningRoom) {
        return new RedisRunningManager(runningRoom, runningCounter);
    }

    @Bean
    public RedisRunningRoom runningRoom() {
        return new RedisRunningRoom(redisTemplate);
    }

    @Bean
    public RedisRunningCounter runningCounter() {
        return new RedisRunningCounter(redisTemplate);
    }
}
