package com.thirdparty.ticketing.global.waiting.room;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thirdparty.ticketing.domain.waitingroom.room.WaitingLine;
import com.thirdparty.ticketing.domain.waitingroom.WaitingMember;
import com.thirdparty.ticketing.global.waiting.ObjectMapperUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

public class RedisWaitingLine implements WaitingLine {

    private static final String WAITING_LINE_KEY = "waiting_line:";

    private final ObjectMapper objectMapper;
    private final ZSetOperations<String, String> waitingLine;

    public RedisWaitingLine(ObjectMapper objectMapper, RedisTemplate<String, String> template) {
        this.objectMapper = objectMapper;
        this.waitingLine = template.opsForZSet();
    }

    @Override
    public void enter(WaitingMember waitingMember) {
        String performanceWaitingLineKey = WAITING_LINE_KEY + waitingMember.getPerformanceId();
        String waitingMemberValue = ObjectMapperUtils.writeValueAsString(objectMapper, waitingMember);
        waitingLine.add(performanceWaitingLineKey, waitingMemberValue, waitingMember.getWaitingCount());
    }

}
