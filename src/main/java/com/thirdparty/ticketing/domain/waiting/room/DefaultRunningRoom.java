package com.thirdparty.ticketing.domain.waiting.room;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.thirdparty.ticketing.domain.waiting.WaitingMember;

public class DefaultRunningRoom implements RunningRoom {

    private final Map<Long, Map<String, WaitingMember>> map = new HashMap<>();

    @Override
    public boolean contains(WaitingMember waitingMember) {
        long performanceId = waitingMember.getPerformanceId();
        if (!map.containsKey(performanceId)) {
            map.put(performanceId, new ConcurrentHashMap<>());
        }
        return map.get(waitingMember.getPerformanceId()).containsKey(waitingMember.getEmail());
    }

    @Override
    public void put(long performanceId, List<WaitingMember> waitingMembers) {
        if (!map.containsKey(performanceId)) {
            map.put(performanceId, new ConcurrentHashMap<>());
        }
        Map<String, WaitingMember> runningMap = map.get(performanceId);
        for (WaitingMember waitingMember : waitingMembers) {
            runningMap.put(waitingMember.getEmail(), waitingMember);
        }
    }
}
