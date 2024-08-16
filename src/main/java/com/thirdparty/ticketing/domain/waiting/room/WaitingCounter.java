package com.thirdparty.ticketing.domain.waiting.room;

import com.thirdparty.ticketing.domain.waiting.WaitingMember;

public interface WaitingCounter {

    /**
     * @return 사용자에게 부여되는 고유한 카운트를 반환한다.
     */
    long getNextCount(WaitingMember waitingMember);
}
