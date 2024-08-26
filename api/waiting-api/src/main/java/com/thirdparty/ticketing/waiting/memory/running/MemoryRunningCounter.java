package com.thirdparty.ticketing.waiting.memory.running;

import java.util.concurrent.ConcurrentMap;

import com.thirdparty.ticketing.waiting.waitingsystem.running.RunningCounter;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MemoryRunningCounter implements RunningCounter {

    private final ConcurrentMap<Long, Long> counter;

    public long getRunningCounter(long performanceId) {
        return counter.getOrDefault(performanceId, 0L);
    }

    public void increment(long performanceId, int size) {
        counter.compute(performanceId, (key, value) -> (value == null) ? size : value + size);
    }
}
