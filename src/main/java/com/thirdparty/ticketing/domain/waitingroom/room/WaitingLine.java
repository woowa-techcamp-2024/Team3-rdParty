package com.thirdparty.ticketing.domain.waitingroom.room;

import com.thirdparty.ticketing.domain.waitingroom.WaitingMember;

public interface WaitingLine {

    /**
     * 사용자를 대기열에 넣는다.
     *
     * @param waitingMember 사용자의 정보
     */
    void enter(WaitingMember waitingMember);
}
