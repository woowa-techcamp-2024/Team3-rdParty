package com.thirdparty.ticketing.global.waitingsystem.memory.running;

import java.util.Set;

import com.thirdparty.ticketing.domain.waitingsystem.running.RunningManager;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MemoryRunningManager implements RunningManager {

    private final MemoryRunningRoom runningRoom;
    private final MemoryRunningCounter runningCounter;

    @Override
    public boolean isReadyToHandle(String email, long performanceId) {
        return runningRoom.contains(email, performanceId);
    }

    @Override
    public long getRunningCount(long performanceId) {
        return runningCounter.getRunningCounter(performanceId);
    }

    @Override
    public long getAvailableToRunning(long performanceId) {
        return runningRoom.getAvailableToRunning(performanceId);
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
