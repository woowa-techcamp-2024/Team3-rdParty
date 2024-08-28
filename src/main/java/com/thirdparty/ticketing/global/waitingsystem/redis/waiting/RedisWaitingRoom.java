package com.thirdparty.ticketing.global.waitingsystem.redis.waiting;

import java.util.Optional;
import java.util.Set;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.thirdparty.ticketing.domain.common.ErrorCode;
import com.thirdparty.ticketing.domain.common.TicketingException;
import com.thirdparty.ticketing.domain.waitingsystem.waiting.WaitingRoom;

public class RedisWaitingRoom implements WaitingRoom {

    private static final String WAITING_ROOM_KEY = "waiting_room:";

    private final HashOperations<String, String, String> waitingRoom;

    public RedisWaitingRoom(StringRedisTemplate redisTemplate) {
        waitingRoom = redisTemplate.opsForHash();
    }

    public boolean enter(String email, long performanceId) {
        return waitingRoom.putIfAbsent(getWaitingRoomKey(performanceId), email, email);
    }

    public void updateMemberInfo(String email, long performanceId, long waitingCount) {
        waitingRoom.put(getWaitingRoomKey(performanceId), email, String.valueOf(waitingCount));
    }

    private String getWaitingRoomKey(long performanceId) {
        return WAITING_ROOM_KEY + performanceId;
    }

    public void removeMemberInfo(String email, long performanceId) {
        waitingRoom.delete(getWaitingRoomKey(performanceId), email);
    }

    public void removeMemberInfo(Set<String> emails, long performanceId) {
        if (emails.isEmpty()) {
            return;
        }
        waitingRoom.delete(getWaitingRoomKey(performanceId), emails.toArray(String[]::new));
    }

    public long getMemberWaitingCount(String email, long performanceId) {
        return Optional.ofNullable(waitingRoom.get(getWaitingRoomKey(performanceId), email))
                .map(Long::parseLong)
                .orElseThrow(() -> new TicketingException(ErrorCode.NOT_FOUND_WAITING_MEMBER));
    }
}
