package com.thirdparty.ticketing.global.config;

import com.thirdparty.ticketing.domain.waitingsystem.WaitingAspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thirdparty.ticketing.domain.common.EventPublisher;
import com.thirdparty.ticketing.domain.waitingsystem.WaitingSystem;
import com.thirdparty.ticketing.domain.waitingsystem.running.RunningManager;
import com.thirdparty.ticketing.domain.waitingsystem.waiting.WaitingManager;
import com.thirdparty.ticketing.global.waitingsystem.redis.running.RedisRunningCounter;
import com.thirdparty.ticketing.global.waitingsystem.redis.running.RedisRunningManager;
import com.thirdparty.ticketing.global.waitingsystem.redis.running.RedisRunningRoom;
import com.thirdparty.ticketing.global.waitingsystem.redis.waiting.RedisWaitingCounter;
import com.thirdparty.ticketing.global.waitingsystem.redis.waiting.RedisWaitingLine;
import com.thirdparty.ticketing.global.waitingsystem.redis.waiting.RedisWaitingManager;
import com.thirdparty.ticketing.global.waitingsystem.redis.waiting.RedisWaitingRoom;

@Configuration
public class WaitingConfig {

    @Bean
    public WaitingAspect waitingAspect(WaitingSystem waitingSystem) {
        return new WaitingAspect(waitingSystem);
    }

    @Bean
    public WaitingSystem waitingSystem(
            WaitingManager waitingManager,
            RunningManager runningManager,
            EventPublisher eventPublisher) {
        return new WaitingSystem(waitingManager, runningManager, eventPublisher);
    }

    @Bean
    public WaitingManager waitingManager(
            RedisWaitingRoom waitingRoom,
            RedisWaitingLine waitingLine,
            RedisWaitingCounter waitingCounter) {
        return new RedisWaitingManager(waitingRoom, waitingCounter, waitingLine);
    }

    @Bean
    public RedisWaitingRoom waitingRoom(
            StringRedisTemplate redisTemplate, ObjectMapper objectMapper) {
        return new RedisWaitingRoom(redisTemplate, objectMapper);
    }

    @Bean
    public RedisWaitingLine waitingLine(
            StringRedisTemplate redisTemplate, ObjectMapper objectMapper) {
        return new RedisWaitingLine(redisTemplate, objectMapper);
    }

    @Bean
    public RedisWaitingCounter waitingCounter(StringRedisTemplate redisTemplate) {
        return new RedisWaitingCounter(redisTemplate);
    }

    @Bean
    public RunningManager runningManager(
            RedisRunningRoom runningRoom, RedisRunningCounter runningCounter) {
        return new RedisRunningManager(runningRoom, runningCounter);
    }

    @Bean
    public RedisRunningRoom runningRoom(StringRedisTemplate redisTemplate) {
        return new RedisRunningRoom(redisTemplate);
    }

    @Bean
    public RedisRunningCounter runningCounter(StringRedisTemplate redisTemplate) {
        return new RedisRunningCounter(redisTemplate);
    }
}
