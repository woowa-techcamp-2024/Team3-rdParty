package com.thirdparty.ticketing.global.waitingsystem.redis.running;

import java.util.Set;

import com.thirdparty.ticketing.domain.waitingsystem.running.RunningManager;

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
    public void enterRunningRoom(long performanceId, Set<String> emails) {
        runningCounter.increment(performanceId, emails.size());
        runningRoom.enter(performanceId, emails);
    }

    @Override
    public void pullOutRunningMember(String email, long performanceId) {
        runningRoom.pullOutRunningMember(email, performanceId);
    }

    @Override
    public Set<String> removeExpiredMemberInfo(long performanceId) {
        return runningRoom.removeExpiredMemberInfo(performanceId);
    }

    @Override
    public void updateRunningMemberExpiredTime(String email, long performanceId) {
        runningRoom.updateRunningMemberExpiredTime(email, performanceId);
    }
}
