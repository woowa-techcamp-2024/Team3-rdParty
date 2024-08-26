package com.thirdparty.ticketing.waiting.waitingsystem.running;

import java.util.Set;

import com.thirdparty.ticketing.waiting.waitingsystem.waiting.WaitingMember;

public interface RunningManager {
    boolean isReadyToHandle(String email, long performanceId);

    long getRunningCount(long performanceId);

    long getAvailableToRunning(long performanceId);

    void enterRunningRoom(long performanceId, Set<WaitingMember> waitingMembers);

    void pullOutRunningMember(String email, long performanceId);

    Set<String> removeExpiredMemberInfo(long performanceId);
}
