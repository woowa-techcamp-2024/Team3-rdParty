package com.thirdparty.ticketing.domain.waitingsystem.waiting;

import java.util.Set;

public interface WaitingManager {
    void enterWaitingRoom(String email, long performanceId);

    void removeMemberInfo(String email, long performanceId);

    void removeMemberInfo(Set<String> emails, long performanceId);

    long getMemberWaitingCount(String email, long performanceId);

    Set<String> pullOutMemberEmails(long performanceId, long availableToRunning);
}
