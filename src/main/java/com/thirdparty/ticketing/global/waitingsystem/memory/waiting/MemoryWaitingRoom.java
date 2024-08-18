package com.thirdparty.ticketing.global.waitingsystem.memory.waiting;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.thirdparty.ticketing.domain.waitingsystem.waiting.WaitingMember;
import com.thirdparty.ticketing.domain.waitingsystem.waiting.WaitingRoom;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MemoryWaitingRoom implements WaitingRoom {

    private final ConcurrentMap<Long, ConcurrentMap<String, WaitingMember>> room;

    public boolean enter(String email, long performanceId) {
        return room.computeIfAbsent(performanceId, k -> new ConcurrentHashMap<>())
                        .putIfAbsent(email, new WaitingMember(email, performanceId))
                == null;
    }

    public void updateMemberInfo(WaitingMember waitingMember) {
        room.computeIfAbsent(waitingMember.getPerformanceId(), k -> new ConcurrentHashMap<>())
                .put(waitingMember.getEmail(), waitingMember);
    }
}
