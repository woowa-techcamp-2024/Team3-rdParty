package com.thirdparty.ticketing.domain.waitingsystem;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.thirdparty.ticketing.domain.common.EventPublisher;
import com.thirdparty.ticketing.domain.waitingsystem.running.RunningManager;
import com.thirdparty.ticketing.domain.waitingsystem.waiting.WaitingManager;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class WaitingSystem {

    private final Map<Long, PollingEvent> pollingEventCache = new ConcurrentHashMap<>();
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
        long memberWaitingCount = waitingManager.getMemberWaitingCount(email, performanceId);
        long runningCount = runningManager.getRunningCount(performanceId);
        long remainingCount = memberWaitingCount - runningCount;
        PollingEvent pollingEvent =
                pollingEventCache.computeIfAbsent(performanceId, PollingEvent::new);
        eventPublisher.publish(pollingEvent);
        if (remainingCount <= 0) {
            eventPublisher.publish(new LastPollingEvent(email, performanceId));
        }
        return remainingCount;
    }

    public void moveUserToRunning(long performanceId) {
        Set<String> removeMemberEmails = runningManager.removeExpiredMemberInfo(performanceId);
        waitingManager.removeMemberInfo(removeMemberEmails, performanceId);
        long availableToRunning = runningManager.getAvailableToRunning(performanceId);
        Set<String> emails = waitingManager.pullOutMemberEmails(performanceId, availableToRunning);
        runningManager.enterRunningRoom(performanceId, emails);
    }

    public void pullOutRunningMember(String email, long performanceId) {
        runningManager.pullOutRunningMember(email, performanceId);
        waitingManager.removeMemberInfo(email, performanceId);
    }

    /**
     * 공연에 해당하는 사용자의 작업 공간 만료 시간을 5분 뒤로 업데이트한다.
     *
     * @param email 사용자의 이메일
     * @param performanceId 공연 ID
     */
    public void updateRunningMemberExpiredTime(String email, long performanceId) {
        runningManager.updateRunningMemberExpiredTime(email, performanceId);
    }
}
