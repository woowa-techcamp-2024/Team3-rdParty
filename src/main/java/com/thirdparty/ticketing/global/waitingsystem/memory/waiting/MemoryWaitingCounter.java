package com.thirdparty.ticketing.global.waitingsystem.memory.waiting;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

import com.thirdparty.ticketing.domain.waitingsystem.waiting.WaitingCounter;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MemoryWaitingCounter implements WaitingCounter {

    private final ConcurrentMap<Long, AtomicLong> counter;

    public long getNextCount(long performanceId) {
        return counter.computeIfAbsent(performanceId, k -> new AtomicLong()).incrementAndGet();
    }
}
