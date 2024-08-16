package com.thirdparty.ticketing.global.waitingsystem;

import com.thirdparty.ticketing.domain.waitingsystem.RunningRoom;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

public class RedisRunningRoom implements RunningRoom {

    private static final String RUNNING_ROOM_KEY = "running_room:";

    private final SetOperations<String, String> runningRoom;

    public RedisRunningRoom(StringRedisTemplate redisTemplate) {
        runningRoom = redisTemplate.opsForSet();
    }

    public boolean contains(String email, long performanceId) {
        return runningRoom.isMember(getRunningRoomKey(performanceId), email);
    }

    private String getRunningRoomKey(long performanceId) {
        return RUNNING_ROOM_KEY + performanceId;
    }
}
