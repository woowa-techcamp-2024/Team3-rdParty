package com.thirdparty.ticketing.domain.waitingsystem;

import java.util.Set;

import com.thirdparty.ticketing.domain.common.EventPublisher;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class WaitingSystem {

    private final WaitingManager waitingManager;
    private final RunningManager runningManager;
    private final EventPublisher eventPublisher;

    public boolean isReadyToHandle(String email, long performanceId) {
        return runningManager.isReadyToHandle(email, performanceId);
    }

    public void enterWaitingRoom(String email, long performanceId) {
        waitingManager.enterWaitingRoom(email, performanceId);
    }

    public long getRemainingCount(String email, long performanceId) {
        WaitingMember waitingMember = waitingManager.findWaitingMember(email, performanceId);
        long runningCount = runningManager.getRunningCount(performanceId);
        eventPublisher.publish(new PollingEvent(performanceId));
        return waitingMember.getWaitingCount() - runningCount;
    }

    public void moveUserToRunning(long performanceId) {
        long availableToRunning = runningManager.getAvailableToRunning(performanceId);
        Set<WaitingMember> waitingMembers =
                waitingManager.pullOutMembers(performanceId, availableToRunning);
        runningManager.enterRunningRoom(performanceId, waitingMembers);
    }
}
