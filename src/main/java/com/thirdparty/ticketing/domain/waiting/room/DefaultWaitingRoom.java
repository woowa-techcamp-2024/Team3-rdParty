package com.thirdparty.ticketing.domain.waiting.room;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.thirdparty.ticketing.domain.waitingsystem.waiting.WaitingMember;

public class DefaultWaitingRoom extends WaitingRoom {

    private final Map<Long, Map<String, WaitingMember>> map = new HashMap<>();

    public DefaultWaitingRoom(WaitingLine waitingLine, WaitingCounter waitingCounter) {
        super(waitingLine, waitingCounter);
    }

    @Override
    public List<WaitingMember> pollWaitingMembers(long performanceId, long count) {
        List<WaitingMember> waitingMembers = waitingLine.pollWaitingMembers(performanceId, count);
        for (WaitingMember waitingMember : waitingMembers) {
            map.get(performanceId).remove(waitingMember.getEmail());
        }
        return waitingMembers;
    }

    @Override
    public synchronized long enter(WaitingMember waitingMember) {
        long performanceId = waitingMember.getPerformanceId();
        String email = waitingMember.getEmail();
        if (!map.containsKey(performanceId)) {
            map.put(performanceId, new ConcurrentHashMap<>());
        }
        if (map.get(performanceId).containsKey(email)) {
            return waitingMember.getWaitingCount();
        }
        long counter = waitingCounter.getNextCount(waitingMember);
        waitingMember.setWaitingCount(counter);
        map.get(performanceId).put(email, waitingMember);
        waitingLine.enter(waitingMember);
        return counter;
    }
}
