package com.thirdparty.ticketing.global.waitingroom;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thirdparty.ticketing.domain.waitingroom.RunningRoom;
import com.thirdparty.ticketing.domain.waitingroom.WaitingCounter;
import com.thirdparty.ticketing.domain.waitingroom.WaitingLine;
import com.thirdparty.ticketing.domain.waitingroom.WaitingRoom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;

@TestConfiguration
public class TestRedisConfig {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

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
