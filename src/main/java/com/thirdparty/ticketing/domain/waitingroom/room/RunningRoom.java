package com.thirdparty.ticketing.domain.waitingroom.room;

import com.thirdparty.ticketing.domain.waitingroom.WaitingMember;

public interface RunningRoom {
    boolean contains(WaitingMember waitingMember);
}
