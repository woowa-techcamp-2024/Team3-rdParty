package com.thirdparty.ticketing.domain.waiting.room;

import com.thirdparty.ticketing.domain.waiting.WaitingMember;

public interface RunningRoom {
    boolean contains(WaitingMember waitingMember);
}
