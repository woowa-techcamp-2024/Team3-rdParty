package com.thirdparty.ticketing.domain.waiting.room;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.thirdparty.ticketing.domain.waiting.WaitingMember;

public class DefaultWaitingRoom extends WaitingRoom {

    private final Map<Long, Map<String, WaitingMember>> map = new HashMap<>();
    private final WaitingLine waitingLine;
    private final WaitingCounter waitingCounter;

    public DefaultWaitingRoom(WaitingLine waitingLine, WaitingCounter waitingCounter) {
        super(waitingLine, waitingCounter);
        this.waitingLine = waitingLine;
        this.waitingCounter = waitingCounter;
    }

    @Override
    public long enter(WaitingMember waitingMember) {
        long performanceId = waitingMember.getPerformanceId();
        String email = waitingMember.getEmail();
        if (!map.containsKey(performanceId)) {
            map.put(performanceId, new ConcurrentHashMap<>());
        }
        if (map.get(performanceId).containsKey(email)) {
            return waitingMember.getWaitingCounter();
        }
        long counter = waitingCounter.getNextCount(performanceId);
        waitingMember.setWaitingCounter(counter);
        map.get(performanceId).put(email, waitingMember);
        waitingLine.enter(waitingMember);
        return counter;
    }
}
