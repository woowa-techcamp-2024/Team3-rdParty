package com.thirdparty.ticketing.domain.waiting.manager;

import com.thirdparty.ticketing.domain.waiting.room.RunningRoom;
import com.thirdparty.ticketing.domain.waiting.room.WaitingRoom;
import com.thirdparty.ticketing.domain.waitingsystem.waiting.WaitingMember;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class WaitingManager {

    protected final RunningRoom runningRoom;
    protected final WaitingRoom waitingRoom;

    public boolean isReadyToHandle(WaitingMember waitingMember) {
        return runningRoom.contains(waitingMember);
    }

    /**
     * 사용자를 대기열에 추가하고 남은 순번을 반환한다.
     *
     * @param waitingMember 대기하는 사용자
     * @return 사용자 앞에 남은 순번
     */
    public long enterWaitingRoom(WaitingMember waitingMember) {
        return waitingRoom.enter(waitingMember) - countManagedMember(waitingMember);
    }

    protected abstract long countManagedMember(WaitingMember waitingMember);

    /**
     * 사용자의 남은 순번을 조회한다. 남은 순번이 1이하인 경우 이벤트를 발행한다.
     *
     * @param email 사용자의 이메일
     * @param performanceId 공연 대기 정보 조회를 위한 공연 ID
     * @return 사용자의 남은 순번
     */
    public abstract long getRemainingCount(String email, Long performanceId);
}
