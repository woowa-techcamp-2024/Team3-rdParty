package com.thirdparty.ticketing.global.waitingroom;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thirdparty.ticketing.domain.common.TicketingException;
import com.thirdparty.ticketing.domain.waitingroom.WaitingCounter;
import com.thirdparty.ticketing.domain.waitingroom.WaitingLine;
import com.thirdparty.ticketing.domain.waitingroom.WaitingMember;
import com.thirdparty.ticketing.domain.waitingroom.WaitingRoom;
import java.time.ZonedDateTime;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

public class RedisWaitingRoom extends WaitingRoom {

    private static final String WAITING_ROOM_KEY = "waiting_room:";

    private final HashOperations<String, String, String> waitingRoom;
    private final ObjectMapper objectMapper;

    public RedisWaitingRoom(WaitingLine waitingLine,
                            WaitingCounter waitingCounter,
                            RedisTemplate<String, String> redisTemplate, ObjectMapper objectMapper) {
        super(waitingLine, waitingCounter);
        waitingRoom = redisTemplate.opsForHash();
        this.objectMapper = objectMapper;
    }

    @Override
    public long enter(WaitingMember waitingMember) {
        if(enterWaitingRoomIfNotExists(waitingMember)) {
            waitingMember.updateWaitingInfo(waitingCounter.getNextCount(waitingMember), ZonedDateTime.now());
            waitingLine.enter(waitingMember);
            updateWaitingRoomMember(waitingMember);
        } else {
            String rawValue = waitingRoom.get(getPerformanceWaitingRoomKey(waitingMember), waitingMember.getEmail());
            try {
                waitingMember = objectMapper.readValue(rawValue, WaitingMember.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
        return waitingMember.getWaitingCount();
    }

    private Boolean enterWaitingRoomIfNotExists(WaitingMember waitingMember) {
        String performanceWaitingRoomKey = getPerformanceWaitingRoomKey(waitingMember);
        String value;
        try {
            value = objectMapper.writeValueAsString(waitingMember);
        } catch (JsonProcessingException e) {
            throw new TicketingException("json 직렬화 에러");
        }
        return waitingRoom.putIfAbsent(performanceWaitingRoomKey, waitingMember.getEmail(), value);
    }

    private void updateWaitingRoomMember(WaitingMember waitingMember) {
        String value;
        try {
            value = objectMapper.writeValueAsString(waitingMember);
        } catch (JsonProcessingException e) {
            throw new TicketingException("json 직렬화 에러");
        }
        waitingRoom.put(getPerformanceWaitingRoomKey(waitingMember), waitingMember.getEmail(), value);
    }

    private String getPerformanceWaitingRoomKey(WaitingMember waitingMember) {
        return WAITING_ROOM_KEY + waitingMember.getPerformanceId();
    }
}
