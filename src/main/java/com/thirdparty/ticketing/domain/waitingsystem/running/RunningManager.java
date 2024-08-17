package com.thirdparty.ticketing.domain.waitingsystem.running;

import com.thirdparty.ticketing.domain.waitingsystem.waiting.WaitingMember;
import java.util.Set;

public interface RunningManager {
    boolean isReadyToHandle(String email, long performanceId);

    long getRunningCount(long performanceId);

    long getAvailableToRunning(long performanceId);

    void enterRunningRoom(long performanceId, Set<WaitingMember> waitingMembers);
}
