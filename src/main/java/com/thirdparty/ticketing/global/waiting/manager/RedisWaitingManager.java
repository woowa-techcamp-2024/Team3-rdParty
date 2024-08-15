package com.thirdparty.ticketing.global.waiting.manager;

import com.thirdparty.ticketing.domain.waitingroom.WaitingMember;
import com.thirdparty.ticketing.domain.waitingroom.manager.WaitingManager;
import com.thirdparty.ticketing.domain.waitingroom.room.RunningRoom;
import com.thirdparty.ticketing.domain.waitingroom.room.WaitingRoom;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

public class RedisWaitingManager extends WaitingManager {

    private static final String MANAGED_MEMBER_COUNTER_KEY = "managed_member_counter:";

    private final ValueOperations<String, String> managedMemberCounter;

    public RedisWaitingManager(RunningRoom runningRoom, WaitingRoom waitingRoom,
                               StringRedisTemplate redisTemplate) {
        super(runningRoom, waitingRoom);
        managedMemberCounter = redisTemplate.opsForValue();
    }

    @Override
    protected long countManagedMember(WaitingMember waitingMember) {
        String key = getPerformanceManagedMemberCounterKey(waitingMember);
        managedMemberCounter.setIfAbsent(key, "0");  // todo: 불필요하게 네트워크를 탐. 추후 개선 필요
        return Long.parseLong(managedMemberCounter.get(key));
    }

    private String getPerformanceManagedMemberCounterKey(WaitingMember waitingMember) {
        return MANAGED_MEMBER_COUNTER_KEY + waitingMember.getPerformanceId();
    }
}
