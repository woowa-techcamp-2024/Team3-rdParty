package com.thirdparty.ticketing.global.waitingsystem;

import com.thirdparty.ticketing.domain.waitingsystem.WaitingRoom;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;

public class RedisWaitingRoom implements WaitingRoom {

    private static final String WAITING_COUNTER_KEY = "waiting_counter:";

    private final HashOperations<String, String, String> waitingRoom;
    private final ValueOperations<String, String> waitingCounter;
    private final ZSetOperations<String, String> waitingLine;

    public RedisWaitingRoom(StringRedisTemplate redisTemplate) {
        waitingRoom = redisTemplate.opsForHash();
        waitingCounter = redisTemplate.opsForValue();
        waitingLine = redisTemplate.opsForZSet();
    }

    public long getNextCount(String email, long performanceId) {
        return waitingCounter.increment(getWaitingCounterKey(performanceId), 1);
    }

    private String getWaitingCounterKey(long performanceId) {
        return WAITING_COUNTER_KEY + performanceId;
    }
}
