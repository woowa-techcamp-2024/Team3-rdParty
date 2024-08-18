package com.thirdparty.ticketing.global.waitingsystem.memory.waiting;

import java.time.ZonedDateTime;
import java.util.Set;

import com.thirdparty.ticketing.domain.waitingsystem.waiting.WaitingManager;
import com.thirdparty.ticketing.domain.waitingsystem.waiting.WaitingMember;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MemoryWaitingManager implements WaitingManager {

    private final MemoryWaitingRoom waitingRoom;
    private final MemoryWaitingCounter waitingCounter;
    private final MemoryWaitingLine waitingLine;

    @Override
    public void enterWaitingRoom(String email, long performanceId) {
        if (waitingRoom.enter(email, performanceId)) {
            long waitingCount = waitingCounter.getNextCount(performanceId);
            WaitingMember waitingMember =
                    new WaitingMember(email, performanceId, waitingCount, ZonedDateTime.now());
            waitingRoom.updateMemberInfo(waitingMember);
            waitingLine.enter(waitingMember);
        }
    }

    @Override
    public WaitingMember findWaitingMember(String email, long performanceId) {
        return null;
    }

    @Override
    public Set<WaitingMember> pullOutMembers(long performanceId, long availableToRunning) {
        return Set.of();
    }
}