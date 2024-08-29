package com.thirdparty.ticketing.global.waitingsystem.redis.waiting;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;

import com.thirdparty.ticketing.domain.waitingsystem.waiting.WaitingLine;

public class RedisWaitingLine implements WaitingLine {

    private static final String WAITING_LINE_KEY = "waiting_line:";

    private final ZSetOperations<String, String> waitingLine;

    public RedisWaitingLine(StringRedisTemplate redisTemplate) {
        this.waitingLine = redisTemplate.opsForZSet();
    }

    public void enter(String email, long performanceId, long waitingCount) {
        waitingLine.add(getWaitingLineKey(performanceId), email, waitingCount);
    }

    private String getWaitingLineKey(long performanceId) {
        return WAITING_LINE_KEY + performanceId;
    }

    public Set<String> pullOutMembers(long performanceId, long availableToRunning) {
        return Optional.ofNullable(
                        waitingLine.popMin(getWaitingLineKey(performanceId), availableToRunning))
                .map(set -> set.stream().map(TypedTuple::getValue).collect(Collectors.toSet()))
                .orElseGet(Set::of);
    }
}
