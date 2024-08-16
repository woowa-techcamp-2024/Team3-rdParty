package com.thirdparty.ticketing.global.waiting.room;

import java.util.List;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;

import com.thirdparty.ticketing.domain.waiting.room.RunningRoom;
import com.thirdparty.ticketing.domain.waitingsystem.WaitingMember;

public class RedisRunningRoom implements RunningRoom {

    private static final String RUNNING_ROOM_KEY = "running_room:";

    private final SetOperations<String, String> runningRoom;

    public RedisRunningRoom(RedisTemplate<String, String> redisTemplate) {
        runningRoom = redisTemplate.opsForSet();
    }

    @Override
    public boolean contains(WaitingMember waitingMember) {
        return runningRoom.isMember(
                getPerformanceRunningRoomKey(waitingMember), waitingMember.getEmail());
    }

    @Override
    public void put(long performanceId, List<WaitingMember> waitingMembers) {}

    private String getPerformanceRunningRoomKey(WaitingMember waitingMember) {
        return RUNNING_ROOM_KEY + waitingMember.getPerformanceId();
    }
}
