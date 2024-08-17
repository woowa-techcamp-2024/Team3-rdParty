package com.thirdparty.ticketing.global.waitingsystem.memory.running;

import java.util.concurrent.ConcurrentMap;

import com.thirdparty.ticketing.domain.waitingsystem.running.RunningRoom;
import com.thirdparty.ticketing.domain.waitingsystem.waiting.WaitingMember;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MemoryRunningRoom implements RunningRoom {

    private final ConcurrentMap<Long, ConcurrentMap<String, WaitingMember>> map;

    public boolean contains(String email, long performanceId) {
        if (!map.containsKey(performanceId)) {
            return false;
        }
        return map.get(performanceId).containsKey(email);
    }
}
