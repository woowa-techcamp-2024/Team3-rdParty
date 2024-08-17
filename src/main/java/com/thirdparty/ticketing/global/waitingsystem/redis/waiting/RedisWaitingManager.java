package com.thirdparty.ticketing.global.waitingsystem.redis.waiting;

import com.thirdparty.ticketing.domain.waitingsystem.waiting.WaitingManager;
import com.thirdparty.ticketing.domain.waitingsystem.waiting.WaitingMember;
import java.time.ZonedDateTime;
import java.util.Set;
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
            WaitingMember waitingMember = new WaitingMember(email, performanceId, waitingCount, ZonedDateTime.now());
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
