package com.thirdparty.ticketing.domain.waiting.room;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class DefaultWaitingCounter implements WaitingCounter {

    private final Map<Long, AtomicLong> map = new HashMap<>();

    @Override
    public long getNextCount(Long performanceId) {
        if (!map.containsKey(performanceId)) {
            map.put(performanceId, new AtomicLong());
        }
        return map.get(performanceId).incrementAndGet();
    }
}
