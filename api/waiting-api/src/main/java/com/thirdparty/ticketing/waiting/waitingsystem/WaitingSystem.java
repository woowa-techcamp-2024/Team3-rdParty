package com.thirdparty.ticketing.waiting.waitingsystem;

import java.util.Set;

import com.thirdparty.ticketing.event.EventPublisher;
import com.thirdparty.ticketing.waiting.event.PollingEvent;
import com.thirdparty.ticketing.waiting.waitingsystem.running.RunningManager;
import com.thirdparty.ticketing.waiting.waitingsystem.waiting.WaitingManager;
import com.thirdparty.ticketing.waiting.waitingsystem.waiting.WaitingMember;

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
        Set<String> removeMemberEmails = runningManager.removeExpiredMemberInfo(performanceId);
        waitingManager.removeMemberInfo(removeMemberEmails, performanceId);
        long availableToRunning = runningManager.getAvailableToRunning(performanceId);
        Set<WaitingMember> waitingMembers =
                waitingManager.pullOutMembers(performanceId, availableToRunning);
        runningManager.enterRunningRoom(performanceId, waitingMembers);
    }

    public void pullOutRunningMember(String email, long performanceId) {
        runningManager.pullOutRunningMember(email, performanceId);
        waitingManager.removeMemberInfo(email, performanceId);
    }
}
