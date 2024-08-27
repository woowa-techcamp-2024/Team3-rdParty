package com.thirdparty.ticketing.global.waitingsystem.redis.running;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import com.thirdparty.ticketing.domain.waitingsystem.running.RunningCounter;

public class RedisRunningCounter implements RunningCounter {

    private static final String RUNNING_COUNTER_KEY = "running_counter:";

    private final ValueOperations<String, String> runningCounter;

    public RedisRunningCounter(StringRedisTemplate redisTemplate) {
        this.runningCounter = redisTemplate.opsForValue();
    }

    public void increment(long performanceId, int number) {
        runningCounter.increment(getRunningCounterKey(performanceId), number);
    }

    public long getRunningCount(long performanceId) {
        String key = getRunningCounterKey(performanceId);
        runningCounter.setIfAbsent(key, "0");
        String rawRunningCount = runningCounter.get(key);
        return Long.parseLong(rawRunningCount);
    }

    private String getRunningCounterKey(long performanceId) {
        return RUNNING_COUNTER_KEY + performanceId;
    }
}
