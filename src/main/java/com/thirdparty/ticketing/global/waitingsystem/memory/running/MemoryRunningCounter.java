package com.thirdparty.ticketing.global.waitingsystem.memory.running;

import java.util.concurrent.ConcurrentMap;

import com.thirdparty.ticketing.domain.waitingsystem.running.RunningCounter;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MemoryRunningCounter implements RunningCounter {

    private final ConcurrentMap<Long, Long> counter;

    public long getRunningCounter(long performanceId) {
        return counter.getOrDefault(performanceId, 0L);
    }
}
