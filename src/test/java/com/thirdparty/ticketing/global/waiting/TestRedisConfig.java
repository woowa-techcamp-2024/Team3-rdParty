package com.thirdparty.ticketing.global.waiting;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thirdparty.ticketing.domain.waiting.room.RunningRoom;
import com.thirdparty.ticketing.domain.waiting.room.WaitingCounter;
import com.thirdparty.ticketing.domain.waiting.room.WaitingLine;
import com.thirdparty.ticketing.domain.waiting.room.WaitingRoom;
import com.thirdparty.ticketing.global.waiting.manager.RedisWaitingManager;
import com.thirdparty.ticketing.global.waiting.room.RedisRunningRoom;
import com.thirdparty.ticketing.global.waiting.room.RedisWaitingCounter;
import com.thirdparty.ticketing.global.waiting.room.RedisWaitingLine;
import com.thirdparty.ticketing.global.waiting.room.RedisWaitingRoom;

@TestConfiguration
public class TestRedisConfig {

    @Qualifier("lettuceRedisTemplate")
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired private ObjectMapper objectMapper;

    @Bean
    public RedisWaitingManager waitingManager(RunningRoom runningRoom, WaitingRoom waitingRoom) {
        return new RedisWaitingManager(runningRoom, waitingRoom, redisTemplate);
    }

    @Bean
    public RedisWaitingRoom waitingRoom(WaitingLine waitingLine, WaitingCounter waitingCounter) {
        return new RedisWaitingRoom(waitingLine, waitingCounter, redisTemplate, objectMapper);
    }

    @Bean
    public RedisWaitingLine waitingLine() {
        return new RedisWaitingLine(objectMapper, redisTemplate);
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
