package com.thirdparty.ticketing.global.waiting.manager;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import com.thirdparty.ticketing.domain.waiting.manager.WaitingManager;
import com.thirdparty.ticketing.domain.waiting.room.RunningRoom;
import com.thirdparty.ticketing.domain.waiting.room.WaitingRoom;
import com.thirdparty.ticketing.domain.waitingsystem.WaitingMember;

public class RedisWaitingManager extends WaitingManager {

    private static final String MANAGED_MEMBER_COUNTER_KEY = "managed_member_counter:";

    private final ValueOperations<String, String> managedMemberCounter;

    public RedisWaitingManager(
            RunningRoom runningRoom, WaitingRoom waitingRoom, StringRedisTemplate redisTemplate) {
        super(runningRoom, waitingRoom);
        managedMemberCounter = redisTemplate.opsForValue();
    }

    @Override
    protected long countManagedMember(WaitingMember waitingMember) {
        String key = getPerformanceManagedMemberCounterKey(waitingMember);
        managedMemberCounter.setIfAbsent(key, "0"); // todo: 불필요하게 네트워크를 탐. 추후 개선 필요
        return Long.parseLong(managedMemberCounter.get(key));
    }

    @Override
    public long getRemainingCount(String email, Long performanceId) {
        return 0;
    }

    private String getPerformanceManagedMemberCounterKey(WaitingMember waitingMember) {
        return MANAGED_MEMBER_COUNTER_KEY + waitingMember.getPerformanceId();
    }
}
