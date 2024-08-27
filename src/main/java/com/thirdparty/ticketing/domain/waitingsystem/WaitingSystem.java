package com.thirdparty.ticketing.domain.waitingsystem;

import java.util.Set;

import com.thirdparty.ticketing.domain.common.EventPublisher;
import com.thirdparty.ticketing.domain.waitingsystem.running.RunningManager;
import com.thirdparty.ticketing.domain.waitingsystem.waiting.WaitingManager;
import com.thirdparty.ticketing.domain.waitingsystem.waiting.WaitingMember;

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
        long remainingCount = waitingMember.getWaitingCount() - runningCount;
        eventPublisher.publish(new PollingEvent(performanceId));
        if (remainingCount <= 0) {
            eventPublisher.publish(new LastPollingEvent(email, performanceId));
        }
        return remainingCount;
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

    public void updateRunningMemberExpiredTime(String email, long performanceId) {
        runningManager.updateRunningMemberExpiredTime(email, performanceId);
    }
}
