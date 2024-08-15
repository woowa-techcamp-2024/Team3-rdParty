package com.thirdparty.ticketing.domain.waiting.room;

import java.util.*;
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

    @Override
    public List<WaitingMember> pollWaitingMembers(long performanceId, long count) {
        if (!map.containsKey(performanceId)) {
            return List.of();
        }
        Queue<WaitingMember> queue = map.get(performanceId);
        List<WaitingMember> waitingMembers = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            if (queue.isEmpty()) {
                break;
            }
            waitingMembers.add(queue.poll());
        }
        return waitingMembers;
    }
}
