package com.thirdparty.ticketing.global.waitingsystem.running;

import java.util.Set;

import com.thirdparty.ticketing.domain.waitingsystem.running.RunningManager;
import com.thirdparty.ticketing.domain.waitingsystem.waiting.WaitingMember;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RedisRunningManager implements RunningManager {

    private final RedisRunningRoom runningRoom;

    @Override
    public boolean isReadyToHandle(String email, long performanceId) {
        return runningRoom.contains(email, performanceId);
    }

    @Override
    public long getRunningCount(long performanceId) {
        return 0;
    }

    @Override
    public long getAvailableToRunning(long performanceId) {
        return 0;
    }

    @Override
    public void enterRunningRoom(long performanceId, Set<WaitingMember> waitingMembers) {}
}