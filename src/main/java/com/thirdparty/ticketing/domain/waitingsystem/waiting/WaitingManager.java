package com.thirdparty.ticketing.domain.waitingsystem.waiting;

import java.util.Set;

public interface WaitingManager {
    void enterWaitingRoom(String email, long performanceId);

    WaitingMember findWaitingMember(String email, long performanceId);

    Set<WaitingMember> pullOutMembers(long performanceId, long availableToRunning);

    void removeMemberInfo(String email, long performanceId);
}
