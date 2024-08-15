package com.thirdparty.ticketing.global.waiting.room;

import com.thirdparty.ticketing.domain.waitingroom.WaitingMember;
import com.thirdparty.ticketing.domain.waitingroom.room.WaitingCounter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

public class RedisWaitingCounter implements WaitingCounter {

    private static final String WAITING_COUNTER_KEY = "waiting_counter";

    private final ValueOperations<String, String> counter;

    public RedisWaitingCounter(StringRedisTemplate redisTemplate) {
        this.counter = redisTemplate.opsForValue();
    }

    @Override
    public long getNextCount(WaitingMember waitingMember) {
        String performanceWaitingCounterKey = WAITING_COUNTER_KEY + waitingMember.getPerformanceId();
        return counter.increment(performanceWaitingCounterKey, 1);
    }
}
