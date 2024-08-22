package com.thirdparty.ticketing.global.waitingsystem.redis.running;

import java.util.Set;

import com.thirdparty.ticketing.domain.waitingsystem.running.RunningManager;
import com.thirdparty.ticketing.domain.waitingsystem.waiting.WaitingMember;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RedisRunningManager implements RunningManager {

    private final RedisRunningRoom runningRoom;
    private final RedisRunningCounter runningCounter;

    @Override
    public boolean isReadyToHandle(String email, long performanceId) {
        return runningRoom.contains(email, performanceId);
    }

    @Override
    public long getRunningCount(long performanceId) {
        return runningCounter.getRunningCount(performanceId);
    }

    @Override
    public long getAvailableToRunning(long performanceId) {
        long availableToRunning = runningRoom.getAvailableToRunning(performanceId);
        return availableToRunning < 0 ? 0 : availableToRunning;
    }

    @Override
    public void enterRunningRoom(long performanceId, Set<WaitingMember> waitingMembers) {
        runningCounter.increment(performanceId, waitingMembers.size());
        runningRoom.enter(performanceId, waitingMembers);
    }

    @Override
    public void pullOutRunningMember(String email, long performanceId) {
        runningRoom.pullOutRunningMember(email, performanceId);
    }

    @Override
    public void removeExpiredMemberInfo(long performanceId) {
    }
}
