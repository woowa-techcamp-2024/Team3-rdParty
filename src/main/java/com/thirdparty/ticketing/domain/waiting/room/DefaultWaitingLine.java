package com.thirdparty.ticketing.domain.waiting.room;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.thirdparty.ticketing.domain.waiting.WaitingMember;

public class DefaultWaitingLine implements WaitingLine {

    private final Map<Long, Queue<WaitingMember>> map = new HashMap<>();

    @Override
    public void enter(WaitingMember waitingMember) {
        long performanceId = waitingMember.getPerformanceId();
        if (!map.containsKey(performanceId)) {
            map.put(performanceId, new ConcurrentLinkedQueue<>());
        }
        map.get(performanceId).offer(waitingMember);
    }
}
