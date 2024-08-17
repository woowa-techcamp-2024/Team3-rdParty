package com.thirdparty.ticketing.domain.waitingsystem.running;

import java.util.Set;

import com.thirdparty.ticketing.domain.waitingsystem.waiting.WaitingMember;

public interface RunningManager {
    boolean isReadyToHandle(String email, long performanceId);

    long getRunningCount(long performanceId);

    long getAvailableToRunning(long performanceId);

    void enterRunningRoom(long performanceId, Set<WaitingMember> waitingMembers);
}
