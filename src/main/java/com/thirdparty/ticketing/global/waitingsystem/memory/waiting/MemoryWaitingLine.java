package com.thirdparty.ticketing.global.waitingsystem.memory.waiting;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    public Set<WaitingMember> pullOutMembers(long performanceId, long availableToRunning) {
        return Optional.ofNullable(line.get(performanceId))
                .map(
                        queue ->
                                Stream.generate(queue::poll)
                                        .limit(availableToRunning)
                                        .filter(Objects::nonNull)
                                        .collect(Collectors.toSet()))
                .orElseGet(HashSet::new);
    }
}
