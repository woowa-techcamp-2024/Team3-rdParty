package com.thirdparty.ticketing.global.waitingsystem.redis.waiting;

import java.util.Optional;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thirdparty.ticketing.domain.waitingsystem.waiting.WaitingMember;
import com.thirdparty.ticketing.domain.waitingsystem.waiting.WaitingRoom;
import com.thirdparty.ticketing.global.waitingsystem.ObjectMapperUtils;

public class RedisWaitingRoom implements WaitingRoom {

    private static final String WAITING_ROOM_KEY = "waiting_room:";

    private final HashOperations<String, String, String> waitingRoom;
    private final ObjectMapper objectMapper;

    public RedisWaitingRoom(StringRedisTemplate redisTemplate, ObjectMapper objectMapper) {
        waitingRoom = redisTemplate.opsForHash();
        this.objectMapper = objectMapper;
    }

    public boolean enter(String email, long performanceId) {
        return waitingRoom.putIfAbsent(getWaitingRoomKey(performanceId), email, email);
    }

    public void updateMemberInfo(WaitingMember waitingMember) {
        String value = ObjectMapperUtils.writeValueAsString(objectMapper, waitingMember);
        waitingRoom.put(
                getWaitingRoomKey(waitingMember.getPerformanceId()),
                waitingMember.getEmail(),
                value);
    }

    private String getWaitingRoomKey(long performanceId) {
        return WAITING_ROOM_KEY + performanceId;
    }

    public Optional<WaitingMember> findWaitingMember(String email, long performanceId) {
        return Optional.ofNullable(waitingRoom.get(getWaitingRoomKey(performanceId), email))
                .map(
                        waitingMember ->
                                ObjectMapperUtils.readValue(
                                        objectMapper, waitingMember, WaitingMember.class));
    }
}
