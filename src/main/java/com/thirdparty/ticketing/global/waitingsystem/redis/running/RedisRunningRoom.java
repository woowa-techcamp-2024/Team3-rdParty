package com.thirdparty.ticketing.global.waitingsystem.redis.running;

import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.thirdparty.ticketing.domain.waitingsystem.running.RunningRoom;

public class RedisRunningRoom implements RunningRoom {

    private static final int MAX_RUNNING_ROOM_SIZE = 100;
    private static final String RUNNING_ROOM_KEY = "running_room:";

    private final SetOperations<String, String> runningRoom;

    public RedisRunningRoom(StringRedisTemplate redisTemplate) {
        runningRoom = redisTemplate.opsForSet();
    }

    public boolean contains(String email, long performanceId) {
        return runningRoom.isMember(getRunningRoomKey(performanceId), email);
    }

    public long getAvailableToRunning(long performanceId) {
        return MAX_RUNNING_ROOM_SIZE - runningRoom.size(getRunningRoomKey(performanceId));
    }

    private String getRunningRoomKey(long performanceId) {
        return RUNNING_ROOM_KEY + performanceId;
    }
}
