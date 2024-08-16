package com.thirdparty.ticketing.domain.waiting.manager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.thirdparty.ticketing.domain.waiting.WaitingMember;
import com.thirdparty.ticketing.domain.waiting.room.RunningRoom;
import com.thirdparty.ticketing.domain.waiting.room.WaitingRoom;

public class DefaultWaitingManager extends WaitingManager {

    private final Map<Long, Long> map = new HashMap<>();

    public DefaultWaitingManager(RunningRoom runningRoom, WaitingRoom waitingRoom) {
        super(runningRoom, waitingRoom);
    }

    @Override
    protected long countManagedMember(WaitingMember waitingMember) {
        long performanceId = waitingMember.getPerformanceId();
        if (!map.containsKey(performanceId)) {
            map.put(performanceId, 0L);
        }
        return map.get(performanceId);
    }

    @Override
    public long getRemainingCount(String email, Long performanceId) {
        return 0;
    }

    public void moveWaitingMemberToRunningRoom(long performanceId, long count) {
        List<WaitingMember> waitingMembers = waitingRoom.pollWaitingMembers(performanceId, count);
        long maxCount = 0L;
        for (WaitingMember waitingMember : waitingMembers) {
            maxCount = Math.max(maxCount, waitingMember.getWaitingCount());
        }
        map.put(performanceId, maxCount);
        runningRoom.put(performanceId, waitingMembers);
    }
}
