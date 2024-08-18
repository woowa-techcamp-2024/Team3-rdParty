package com.thirdparty.ticketing.global.waitingsystem.redis.waiting;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thirdparty.ticketing.domain.waitingsystem.waiting.WaitingLine;
import com.thirdparty.ticketing.domain.waitingsystem.waiting.WaitingMember;
import com.thirdparty.ticketing.global.waiting.ObjectMapperUtils;

public class RedisWaitingLine implements WaitingLine {

    private static final String WAITING_LINE_KEY = "waiting_line:";

    private final ZSetOperations<String, String> waitingLine;
    private final ObjectMapper objectMapper;

    public RedisWaitingLine(StringRedisTemplate redisTemplate, ObjectMapper objectMapper) {
        this.waitingLine = redisTemplate.opsForZSet();
        this.objectMapper = objectMapper;
    }

    public void enter(WaitingMember waitingMember) {
        String value = ObjectMapperUtils.writeValueAsString(objectMapper, waitingMember);
        waitingLine.add(
                getWaitingLineKey(waitingMember.getPerformanceId()),
                value,
                waitingMember.getWaitingCount());
    }

    private String getWaitingLineKey(long performanceId) {
        return WAITING_LINE_KEY + performanceId;
    }

    public Set<WaitingMember> pullOutMembers(long performanceId, long availableToRunning) {
        return Optional.ofNullable(waitingLine.popMin(getWaitingLineKey(performanceId), availableToRunning))
                .map(set -> set.stream()
                        .map(value -> ObjectMapperUtils.readValue(objectMapper, value.getValue(), WaitingMember.class))
                        .collect(Collectors.toSet()))
                .orElseGet(Set::of);
    }
}
