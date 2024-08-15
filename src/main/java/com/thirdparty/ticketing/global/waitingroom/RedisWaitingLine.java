package com.thirdparty.ticketing.global.waitingroom;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thirdparty.ticketing.domain.common.TicketingException;
import com.thirdparty.ticketing.domain.waitingroom.WaitingLine;
import com.thirdparty.ticketing.domain.waitingroom.WaitingMember;
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
        String waitingMemberValue;
        try {
            waitingMemberValue = objectMapper.writeValueAsString(waitingMember);
        } catch (JsonProcessingException e) {
            throw new TicketingException("json 직렬화 예외 발생");
        }
        waitingLine.add(performanceWaitingLineKey, waitingMemberValue, waitingMember.getWaitingCount());
    }

}
