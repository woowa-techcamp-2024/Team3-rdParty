package com.thirdparty.ticketing.global.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thirdparty.ticketing.domain.waitingroom.manager.WaitingManager;
import com.thirdparty.ticketing.domain.waitingroom.room.RunningRoom;
import com.thirdparty.ticketing.domain.waitingroom.room.WaitingCounter;
import com.thirdparty.ticketing.domain.waitingroom.room.WaitingLine;
import com.thirdparty.ticketing.domain.waitingroom.room.WaitingRoom;
import com.thirdparty.ticketing.global.waiting.manager.RedisWaitingManager;
import com.thirdparty.ticketing.global.waiting.room.RedisRunningRoom;
import com.thirdparty.ticketing.global.waiting.room.RedisWaitingCounter;
import com.thirdparty.ticketing.global.waiting.room.RedisWaitingLine;
import com.thirdparty.ticketing.global.waiting.room.RedisWaitingRoom;

@Configuration
public class WaitingConfig {

    @Bean
    public WaitingManager waitingManager(
            RunningRoom runningRoom,
            WaitingRoom waitingRoom,
            @Qualifier("lettuceRedisTemplate") StringRedisTemplate redisTemplate) {
        return new RedisWaitingManager(runningRoom, waitingRoom, redisTemplate);
    }

    @Bean
    public WaitingRoom waitingRoom(
            WaitingLine waitingLine,
            WaitingCounter waitingCounter,
            @Qualifier("lettuceRedisTemplate") StringRedisTemplate redisTemplate,
            ObjectMapper objectMapper) {
        return new RedisWaitingRoom(waitingLine, waitingCounter, redisTemplate, objectMapper);
    }

    @Bean
    public WaitingLine waitingLine(
            ObjectMapper objectMapper,
            @Qualifier("lettuceRedisTemplate") StringRedisTemplate redisTemplate) {
        return new RedisWaitingLine(objectMapper, redisTemplate);
    }

    @Bean
    public WaitingCounter waitingCounter(
            @Qualifier("lettuceRedisTemplate") StringRedisTemplate redisTemplate) {
        return new RedisWaitingCounter(redisTemplate);
    }

    @Bean
    public RunningRoom runningRoom(
            @Qualifier("lettuceRedisTemplate") StringRedisTemplate redisTemplate) {
        return new RedisRunningRoom(redisTemplate);
    }
}
