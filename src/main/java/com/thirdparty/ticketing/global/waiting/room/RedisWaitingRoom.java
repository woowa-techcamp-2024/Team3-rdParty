package com.thirdparty.ticketing.global.waiting.room;

import java.time.ZonedDateTime;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thirdparty.ticketing.domain.waitingroom.WaitingMember;
import com.thirdparty.ticketing.domain.waitingroom.room.WaitingCounter;
import com.thirdparty.ticketing.domain.waitingroom.room.WaitingLine;
import com.thirdparty.ticketing.domain.waitingroom.room.WaitingRoom;
import com.thirdparty.ticketing.global.waiting.ObjectMapperUtils;

public class RedisWaitingRoom extends WaitingRoom {

    private static final String WAITING_ROOM_KEY = "waiting_room:";

    private final HashOperations<String, String, String> waitingRoom;
    private final ObjectMapper objectMapper;

    public RedisWaitingRoom(
            WaitingLine waitingLine,
            WaitingCounter waitingCounter,
            RedisTemplate<String, String> redisTemplate,
            ObjectMapper objectMapper) {
        super(waitingLine, waitingCounter);
        waitingRoom = redisTemplate.opsForHash();
        this.objectMapper = objectMapper;
    }

    @Override
    public long enter(WaitingMember waitingMember) {
        if (enterWaitingRoomIfNotExists(waitingMember)) {
            waitingMember.updateWaitingInfo(
                    waitingCounter.getNextCount(waitingMember), ZonedDateTime.now());
            waitingLine.enter(waitingMember);
            updateWaitingRoomMember(waitingMember);
        } else {
            String rawValue =
                    waitingRoom.get(
                            getPerformanceWaitingRoomKey(waitingMember), waitingMember.getEmail());
            waitingMember =
                    ObjectMapperUtils.readValue(objectMapper, rawValue, WaitingMember.class);
        }
        return waitingMember.getWaitingCount();
    }

    private Boolean enterWaitingRoomIfNotExists(WaitingMember waitingMember) {
        String performanceWaitingRoomKey = getPerformanceWaitingRoomKey(waitingMember);
        String value = ObjectMapperUtils.writeValueAsString(objectMapper, waitingMember);
        return waitingRoom.putIfAbsent(performanceWaitingRoomKey, waitingMember.getEmail(), value);
    }

    private void updateWaitingRoomMember(WaitingMember waitingMember) {
        String value = ObjectMapperUtils.writeValueAsString(objectMapper, waitingMember);
        waitingRoom.put(
                getPerformanceWaitingRoomKey(waitingMember), waitingMember.getEmail(), value);
    }

    private String getPerformanceWaitingRoomKey(WaitingMember waitingMember) {
        return WAITING_ROOM_KEY + waitingMember.getPerformanceId();
    }
}
