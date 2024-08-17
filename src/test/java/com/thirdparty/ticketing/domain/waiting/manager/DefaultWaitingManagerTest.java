package com.thirdparty.ticketing.domain.waiting.manager;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.thirdparty.ticketing.domain.waiting.room.DefaultRunningRoom;
import com.thirdparty.ticketing.domain.waiting.room.DefaultWaitingCounter;
import com.thirdparty.ticketing.domain.waiting.room.DefaultWaitingLine;
import com.thirdparty.ticketing.domain.waiting.room.DefaultWaitingRoom;
import com.thirdparty.ticketing.domain.waitingsystem.waiting.WaitingMember;

class DefaultWaitingManagerTest {

    @Test
    @DisplayName("디폴트 웨이팅 매니저 멀티스레드 테스트")
    void testMultithreadedEnterAndMove() {
        DefaultRunningRoom runningRoom = new DefaultRunningRoom();
        DefaultWaitingLine waitingLine = new DefaultWaitingLine();
        DefaultWaitingCounter waitingCounter = new DefaultWaitingCounter();
        DefaultWaitingRoom waitingRoom = new DefaultWaitingRoom(waitingLine, waitingCounter);
        DefaultWaitingManager waitingManager = new DefaultWaitingManager(runningRoom, waitingRoom);

        int numPerformances = 3;
        int numMembersPerPerformance = 100000;
        ExecutorService executorService = Executors.newFixedThreadPool(10);

        // 멤버 입장
        for (int performanceId = 1; performanceId <= numPerformances; performanceId++) {
            long perfId = performanceId;
            for (int i = 0; i < numMembersPerPerformance; i++) {
                final int memberId = i;
                executorService.execute(
                        () -> {
                            WaitingMember member =
                                    new WaitingMember("user" + memberId + "@example.com", perfId);
                            waitingManager.enterWaitingRoom(member);
                        });
            }
        }

        executorService.shutdown();
        while (!executorService.isTerminated()) {
            Thread.yield();
        }

        // 멤버를 옮기기 전에 WaitingLine 에 제대로 순서대로 count 맞춰서 들어갔는지 확인하는 테스트
        for (int performanceId = 1; performanceId <= numPerformances; performanceId++) {
            List<WaitingMember> members =
                    waitingLine.pollWaitingMembers(performanceId, numMembersPerPerformance);
            assertThat(members)
                    .as("공연 %d의 대기열에 모든 멤버가 있어야 합니다", performanceId)
                    .hasSize(numMembersPerPerformance);

            for (int i = 0; i < numMembersPerPerformance; i++) {
                WaitingMember member = members.get(i);
                assertThat(member.getWaitingCount())
                        .as("공연 %d의 멤버는 정확한 대기 번호를 가져야 합니다", performanceId)
                        .isEqualTo(i + 1);
            }

            // 확인 후 다시 WaitingLine 에 넣어줍니다 (이 부분은 pollWaitingMembers 메서드의 동작에 따라 필요할 수 있습니다)
            for (WaitingMember member : members) {
                waitingLine.enter(member);
            }
        }

        // 멤버를 실행 중인 룸으로 이동하고 확인
        for (int performanceId = 1; performanceId <= numPerformances; performanceId++) {
            waitingManager.moveWaitingMemberToRunningRoom(performanceId, numMembersPerPerformance);

            // 모든 멤버가 대기실에서 이동되었는지 확인
            List<WaitingMember> remainingMembers =
                    waitingRoom.pollWaitingMembers(performanceId, numMembersPerPerformance);
            assertThat(remainingMembers)
                    .as("공연 %d의 모든 멤버가 대기실에서 이동되어야 합니다", performanceId)
                    .isEmpty();
        }
    }
}
