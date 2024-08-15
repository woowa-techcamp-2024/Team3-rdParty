package com.thirdparty.ticketing.domain.waitingroom;

public interface WaitingLine {

    /**
     * 사용자를 대기열에 넣는다.
     *
     * @param waitingMember 사용자의 정보
     * @param waitingCounter 사용자의 고유한 카운트 값
     */
    void enter(WaitingMember waitingMember, long waitingCounter);
}
