package com.thirdparty.ticketing.global.waiting.room;

import java.util.List;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thirdparty.ticketing.domain.waiting.room.WaitingLine;
import com.thirdparty.ticketing.domain.waitingsystem.WaitingMember;
import com.thirdparty.ticketing.global.waiting.ObjectMapperUtils;

public class RedisWaitingLine implements WaitingLine {

    private static final String WAITING_LINE_KEY = "waiting_line:";

    private final ObjectMapper objectMapper;
    private final ZSetOperations<String, String> waitingLine;

    public RedisWaitingLine(ObjectMapper objectMapper, StringRedisTemplate redisTemplate) {
        this.objectMapper = objectMapper;
        this.waitingLine = redisTemplate.opsForZSet();
    }

    @Override
    public void enter(WaitingMember waitingMember) {
        String performanceWaitingLineKey = WAITING_LINE_KEY + waitingMember.getPerformanceId();
        String waitingMemberValue =
                ObjectMapperUtils.writeValueAsString(objectMapper, waitingMember);
        waitingLine.add(
                performanceWaitingLineKey, waitingMemberValue, waitingMember.getWaitingCount());
    }

    @Override
    public List<WaitingMember> pollWaitingMembers(long performanceId, long count) {
        return List.of();
    }
}
