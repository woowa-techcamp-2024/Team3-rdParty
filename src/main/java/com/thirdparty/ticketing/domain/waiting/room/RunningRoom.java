package com.thirdparty.ticketing.domain.waiting.room;

import java.util.List;

import com.thirdparty.ticketing.domain.waitingsystem.waiting.WaitingMember;

public interface RunningRoom {
    boolean contains(WaitingMember waitingMember);

    void put(long performanceId, List<WaitingMember> waitingMembers);
}
