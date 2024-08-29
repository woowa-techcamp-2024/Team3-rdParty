package com.thirdparty.ticketing.global.waitingsystem.memory.running;

import java.time.ZonedDateTime;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import com.thirdparty.ticketing.domain.waitingsystem.running.RunningRoom;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MemoryRunningRoom implements RunningRoom {

    private static final int MAX_MEMORY_RUNNING_ROOM_SIZE = 100;
    private static final int EXPIRED_MINUTE = 5;

    private final ConcurrentMap<Long, ConcurrentMap<String, ZonedDateTime>> room;

    public boolean contains(String email, long performanceId) {
        if (!room.containsKey(performanceId)) {
            return false;
        }
        return room.get(performanceId).containsKey(email);
    }

    public long getAvailableToRunning(long performanceId) {
        return Math.max(
                0,
                MAX_MEMORY_RUNNING_ROOM_SIZE
                        - (room.containsKey(performanceId) ? room.get(performanceId).size() : 0));
    }

    public void enter(long performanceId, Set<String> emails) {
        room.compute(
                performanceId,
                (key, room) -> {
                    ConcurrentMap<String, ZonedDateTime> runningRoom =
                            (room != null) ? room : new ConcurrentHashMap<>();
                    emails.forEach(
                            email -> runningRoom.put(email, ZonedDateTime.now().plusSeconds(30)));
                    return runningRoom;
                });
    }

    public void pullOutRunningMember(String email, long performanceId) {
        room.computeIfPresent(
                performanceId,
                (key, room) -> {
                    room.remove(email);
                    return room;
                });
    }

    public Set<String> removeExpiredMemberInfo(long performanceId) {
        ConcurrentMap<String, ZonedDateTime> performanceRoom = room.get(performanceId);
        if (performanceRoom == null) {
            return Set.of();
        }

        ZonedDateTime now = ZonedDateTime.now();
        Set<String> removeMemberEmails =
                performanceRoom.entrySet().stream()
                        .filter(entry -> entry.getValue().isBefore(now))
                        .map(Entry::getKey)
                        .collect(Collectors.toSet());
        removeMemberEmails.forEach(performanceRoom::remove);
        return removeMemberEmails;
    }

    public void updateRunningMemberExpiredTime(String email, long performanceId) {
        room.computeIfPresent(
                performanceId,
                (key, room) -> {
                    room.put(email, ZonedDateTime.now().plusMinutes(EXPIRED_MINUTE));
                    return room;
                });
    }
}
