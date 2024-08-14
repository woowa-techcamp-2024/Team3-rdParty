package com.thirdparty.ticketing.domain.waitingroom;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class WaitingManager {

    private final RunningRoom runningRoom;
    private final WaitingRoom waitingRoom;

    public boolean isReadyToHandle(UserInfo userInfo) {
        return runningRoom.contains(userInfo);
    }

    public long enterWaitingRoom(UserInfo userInfo) {
        return waitingRoom.enter(userInfo);
    }
}
