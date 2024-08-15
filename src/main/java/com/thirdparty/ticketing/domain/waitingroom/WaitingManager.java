package com.thirdparty.ticketing.domain.waitingroom;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class WaitingManager {

    private final RunningRoom runningRoom;
    private final WaitingRoom waitingRoom;

    public boolean isReadyToHandle(WaitingMember waitingMember) {
        return runningRoom.contains(waitingMember);
    }

    public long enterWaitingRoom(WaitingMember waitingMember) {
        return waitingRoom.enter(waitingMember);
    }
}
