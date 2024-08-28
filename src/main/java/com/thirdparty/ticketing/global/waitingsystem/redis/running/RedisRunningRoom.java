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
    private static final int MINIMUM_RUNNING_TIME = 30;

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
        ZonedDateTime minimumRunningTime = ZonedDateTime.now().plusSeconds(MINIMUM_RUNNING_TIME);
        Set<TypedTuple<String>> collect =
                waitingMembers.stream()
                        .map(
                                member ->
                                        TypedTuple.of(
                                                member.getEmail(),
                                                (double) minimumRunningTime.toEpochSecond()))
                        .collect(Collectors.toSet());
        runningRoom.add(getRunningRoomKey(performanceId), collect);
    }

    private String getRunningRoomKey(long performanceId) {
        return RUNNING_ROOM_KEY + performanceId;
    }

    public void pullOutRunningMember(String email, long performanceId) {
        runningRoom.remove(getRunningRoomKey(performanceId), email);
    }

    /**
     * 주어진 공연에 해당하는 러닝룸에서 만료 시간이 현재 시간 이전인 사람들을 제거한다.
     *
     * @param performanceId
     * @return
     */
    public Set<String> removeExpiredMemberInfo(long performanceId) {
        long removeRange = ZonedDateTime.now().toEpochSecond();
        String runningRoomKey = getRunningRoomKey(performanceId);
        Set<String> removedMemberEmails = runningRoom.rangeByScore(runningRoomKey, 0, removeRange);
        runningRoom.removeRangeByScore(runningRoomKey, 0, removeRange);
        return removedMemberEmails;
    }

    /**
     * 주어진 공연 - 이메일에 해당하는 사용자의 러닝룸 만료시간을 5분뒤로 업데이트 한다. 동시성 문제가 발생할 수 있다.
     *
     * @param email 사용자의 이메일
     * @param performanceId 공연 ID
     */
    public void updateRunningMemberExpiredTime(String email, long performanceId) {
        if (runningRoom.score(getRunningRoomKey(performanceId), email) != null) {
            runningRoom.add(
                    getRunningRoomKey(performanceId),
                    email,
                    ZonedDateTime.now().plusMinutes(EXPIRED_MINUTE).toEpochSecond());
        }
    }
}
