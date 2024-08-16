package com.thirdparty.ticketing.domain.waitingroom;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class WaitingRoom {

    private final WaitingLine waitingLine;
    private final WaitingCounter waitingCounter;

    /**
     * 사용자를 대기열에 넣고, 대기공간에 보관한다.
     *
     * @param waitingMember 사용자의 정보
     * @return 사용자의 남은 순번을 반환한다.
     */
    public abstract long enter(WaitingMember waitingMember);
}
