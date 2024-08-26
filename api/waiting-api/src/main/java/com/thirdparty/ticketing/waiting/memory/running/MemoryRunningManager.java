package com.thirdparty.ticketing.waiting.memory.running;

import java.util.Set;

import com.thirdparty.ticketing.waiting.waitingsystem.running.RunningManager;
import com.thirdparty.ticketing.waiting.waitingsystem.waiting.WaitingMember;

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
    public void enterRunningRoom(long performanceId, Set<WaitingMember> waitingMembers) {
        waitingMembers.forEach(WaitingMember::enter);
        runningCounter.increment(performanceId, waitingMembers.size());
        runningRoom.enter(performanceId, waitingMembers);
    }

    @Override
    public void pullOutRunningMember(String email, long performanceId) {
        runningRoom.pullOutRunningMember(email, performanceId);
    }

    @Override
    public Set<String> removeExpiredMemberInfo(long performanceId) {
        return runningRoom.removeExpiredMemberInfo(performanceId);
    }
}
