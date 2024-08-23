package com.thirdparty.ticketing.global.waitingsystem.redis.running;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;

import com.thirdparty.ticketing.domain.waitingsystem.running.RunningRoom;
import com.thirdparty.ticketing.domain.waitingsystem.waiting.WaitingMember;

public class RedisRunningRoom implements RunningRoom {

    private static final int MAX_RUNNING_ROOM_SIZE = 100;
    private static final String RUNNING_ROOM_KEY = "running_room:";
    private static final int EXPIRED_MINUTE = 5;

    private final ZSetOperations<String, String> runningRoom;

    public RedisRunningRoom(StringRedisTemplate redisTemplate) {
        runningRoom = redisTemplate.opsForZSet();
    }

    public boolean contains(String email, long performanceId) {
        return Optional.ofNullable(runningRoom.score(getRunningRoomKey(performanceId), email))
                .isPresent();
    }

    public long getAvailableToRunning(long performanceId) {
        return MAX_RUNNING_ROOM_SIZE - runningRoom.size(getRunningRoomKey(performanceId));
    }

    public void enter(long performanceId, Set<WaitingMember> waitingMembers) {
        if (waitingMembers.isEmpty()) {
            return;
        }
        Set<TypedTuple<String>> collect =
                waitingMembers.stream()
                        .map(
                                member ->
                                        TypedTuple.of(
                                                member.getEmail(),
                                                (double) ZonedDateTime.now().toEpochSecond()))
                        .collect(Collectors.toSet());
        runningRoom.add(getRunningRoomKey(performanceId), collect);
    }

    private String getRunningRoomKey(long performanceId) {
        return RUNNING_ROOM_KEY + performanceId;
    }

    public void pullOutRunningMember(String email, long performanceId) {
        runningRoom.remove(getRunningRoomKey(performanceId), email);
    }

    public void removeExpiredMemberInfo(long performanceId) {
        long removeRange = ZonedDateTime.now().minusMinutes(EXPIRED_MINUTE).toEpochSecond();
        runningRoom.removeRangeByScore(getRunningRoomKey(performanceId), 0, removeRange);
    }
}
