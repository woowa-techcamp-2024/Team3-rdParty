package com.thirdparty.ticketing.global.waitingsystem.redis.waiting;

import java.time.ZonedDateTime;
import java.util.Set;

import com.thirdparty.ticketing.domain.common.ErrorCode;
import com.thirdparty.ticketing.domain.common.TicketingException;
import com.thirdparty.ticketing.domain.waitingsystem.waiting.WaitingManager;
import com.thirdparty.ticketing.domain.waitingsystem.waiting.WaitingMember;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class RedisWaitingManager implements WaitingManager {

    private final RedisWaitingRoom waitingRoom;
    private final RedisWaitingCounter waitingCounter;
    private final RedisWaitingLine waitingLine;

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
}
