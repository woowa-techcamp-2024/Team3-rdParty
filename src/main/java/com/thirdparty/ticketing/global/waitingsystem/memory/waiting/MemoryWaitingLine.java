package com.thirdparty.ticketing.global.waitingsystem.memory.waiting;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;

import com.thirdparty.ticketing.domain.waitingsystem.waiting.WaitingLine;
import com.thirdparty.ticketing.domain.waitingsystem.waiting.WaitingMember;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MemoryWaitingLine implements WaitingLine {

    private final ConcurrentMap<Long, ConcurrentLinkedQueue<WaitingMember>> line;

    public void enter(WaitingMember waitingMember) {
        line.computeIfAbsent(waitingMember.getPerformanceId(), k -> new ConcurrentLinkedQueue<>())
                .offer(waitingMember);
    }
}
