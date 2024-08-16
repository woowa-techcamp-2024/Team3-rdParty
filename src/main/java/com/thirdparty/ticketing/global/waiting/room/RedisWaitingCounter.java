package com.thirdparty.ticketing.global.waiting.room;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import com.thirdparty.ticketing.domain.waiting.WaitingMember;
import com.thirdparty.ticketing.domain.waiting.room.WaitingCounter;

public class RedisWaitingCounter implements WaitingCounter {

    private static final String WAITING_COUNTER_KEY = "waiting_counter";

    private final ValueOperations<String, String> counter;

    public RedisWaitingCounter(StringRedisTemplate redisTemplate) {
        this.counter = redisTemplate.opsForValue();
    }

    @Override
    public long getNextCount(WaitingMember waitingMember) {
        String performanceWaitingCounterKey =
                WAITING_COUNTER_KEY + waitingMember.getPerformanceId();
        return counter.increment(performanceWaitingCounterKey, 1);
    }
}
