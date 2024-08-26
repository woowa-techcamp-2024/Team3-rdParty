package com.thirdparty.ticketing.waiting.memory.waiting;

import java.time.ZonedDateTime;
import java.util.Set;

import com.thirdparty.ticketing.exception.ErrorCode;
import com.thirdparty.ticketing.exception.TicketingException;
import com.thirdparty.ticketing.waiting.waitingsystem.waiting.WaitingManager;
import com.thirdparty.ticketing.waiting.waitingsystem.waiting.WaitingMember;

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
        return waitingRoom
                .findWaitingMember(email, performanceId)
                .orElseThrow(() -> new TicketingException(ErrorCode.NOT_FOUND_WAITING_MEMBER));
    }

    @Override
    public Set<WaitingMember> pullOutMembers(long performanceId, long availableToRunning) {
        return waitingLine.pullOutMembers(performanceId, availableToRunning);
    }

    @Override
    public void removeMemberInfo(String email, long performanceId) {
        waitingRoom.removeMemberInfo(email, performanceId);
    }

    @Override
    public void removeMemberInfo(Set<String> emails, long performanceId) {
        waitingRoom.removeMemberInfo(emails, performanceId);
    }
}
