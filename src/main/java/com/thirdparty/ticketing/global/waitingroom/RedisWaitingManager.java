package com.thirdparty.ticketing.global.waitingroom;

import com.thirdparty.ticketing.domain.waitingroom.RunningRoom;
import com.thirdparty.ticketing.domain.waitingroom.WaitingManager;
import com.thirdparty.ticketing.domain.waitingroom.WaitingMember;
import com.thirdparty.ticketing.domain.waitingroom.WaitingRoom;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

public class RedisWaitingManager extends WaitingManager {

    private static final String MANAGED_MEMBER_COUNTER_KEY = "managed_member_counter:";

    private final ValueOperations<String, String> managedMemberCounter;

    public RedisWaitingManager(RunningRoom runningRoom, WaitingRoom waitingRoom,
                               RedisTemplate<String, String> redisTemplate) {
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
