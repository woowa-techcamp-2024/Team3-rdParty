package com.thirdparty.ticketing.domain.waiting.room;

import java.util.List;

import com.thirdparty.ticketing.domain.waitingsystem.waiting.WaitingMember;

public interface WaitingLine {

    /**
     * 사용자를 대기열에 넣는다.
     *
     * @param waitingMember 사용자의 정보
     */
    void enter(WaitingMember waitingMember);

    List<WaitingMember> pollWaitingMembers(long performanceId, long count);
}
