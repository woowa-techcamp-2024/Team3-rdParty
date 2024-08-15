package com.thirdparty.ticketing.domain.waitingroom.manager;

import com.thirdparty.ticketing.domain.waitingroom.WaitingMember;
import com.thirdparty.ticketing.domain.waitingroom.room.RunningRoom;
import com.thirdparty.ticketing.domain.waitingroom.room.WaitingRoom;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class WaitingManager {

    private final RunningRoom runningRoom;
    private final WaitingRoom waitingRoom;

    public boolean isReadyToHandle(WaitingMember waitingMember) {
        return runningRoom.contains(waitingMember);
    }

    /**
     * 사용자를 대기열에 추가하고 남은 순번을 반환한다.
     *
     * @param waitingMember
     * @return 사용자 앞에 남은 순번
     */
    public long enterWaitingRoom(WaitingMember waitingMember) {
        return waitingRoom.enter(waitingMember) - countManagedMember(waitingMember);
    }

    protected abstract long countManagedMember(WaitingMember waitingMember);
}
