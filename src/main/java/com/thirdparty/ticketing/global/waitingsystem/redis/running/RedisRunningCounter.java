package com.thirdparty.ticketing.global.waitingsystem.redis.running;

import com.thirdparty.ticketing.domain.waitingsystem.running.RunningCounter;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

public class RedisRunningCounter implements RunningCounter {

    private static final String RUNNING_COUNTER = "running_counter:";

    private final ValueOperations<String, String> runningCounter;

    public RedisRunningCounter(StringRedisTemplate redisTemplate) {
        this.runningCounter = redisTemplate.opsForValue();
    }

    public long getRunningCount(long performanceId) {
        String key = getRunningCounterKey(performanceId);
        runningCounter.setIfAbsent(key, "0");
        String rawRunningCount = runningCounter.get(key);
        return Long.parseLong(rawRunningCount);
    }

    private String getRunningCounterKey(long performanceId) {
        return RUNNING_COUNTER + performanceId;
    }
}
