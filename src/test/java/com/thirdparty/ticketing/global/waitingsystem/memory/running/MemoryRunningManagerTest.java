package com.thirdparty.ticketing.global.waitingsystem.memory.running;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.thirdparty.ticketing.domain.waitingsystem.waiting.WaitingMember;

class MemoryRunningManagerTest {

    private MemoryRunningManager runningManager;
    private ConcurrentMap<Long, ConcurrentMap<String, WaitingMember>> room;
    private ConcurrentMap<Long, Long> counter;

    @BeforeEach
    void setUp() {
        room = new ConcurrentHashMap<>();
        counter = new ConcurrentHashMap<>();
        MemoryRunningRoom runningRoom = new MemoryRunningRoom(room);
        MemoryRunningCounter runningCounter = new MemoryRunningCounter(counter);
        runningManager = new MemoryRunningManager(runningRoom, runningCounter);
    }

    @Nested
    @DisplayName("러닝 카운트 조회 시")
    class GetRunningCounterTest {

        @Test
        @DisplayName("작업 가능 공간으로 진입한 인원 수를 반환한다.")
        void getRunningCounter() {
            // given
            long performanceId = 1;
            counter.put(performanceId, 44L);

            // when
            long runningCount = runningManager.getRunningCount(performanceId);

            // then
            assertThat(runningCount).isEqualTo(44L);
        }

        @Test
        @DisplayName("카운트가 존재하지 않으면 0부터 시작한다.")
        void startCounterWithZeroValue() {
            // given
            long performanceId = 1;

            // when
            long runningCount = runningManager.getRunningCount(performanceId);

            // then
            assertThat(runningCount).isEqualTo(0L);
        }
    }

    @Nested
    @DisplayName("작업 가능 인원 조회 시")
    class GetAvailableToRunning {

        @Test
        @DisplayName("0보다 작으면 0을 반환한다.")
        void returnZero_WhenLessThanZero() {
            // given
            long performanceId = 1;
            Set<WaitingMember> waitingMembers = new HashSet<>();
            for (int i = 0; i < 150; i++) {
                waitingMembers.add(
                        new WaitingMember(
                                "email" + i + "@email.com", performanceId, i, ZonedDateTime.now()));
            }
            runningManager.enterRunningRoom(performanceId, waitingMembers);

            // when
            long availableToRunning = runningManager.getAvailableToRunning(performanceId);

            // then
            assertThat(availableToRunning).isEqualTo(0);
        }

        @Test
        @DisplayName("0보다 크면 그대로 반환한다.")
        void returnAvailable_WhenGreaterThanZero() {
            // given
            long performanceId = 1;
            Set<WaitingMember> waitingMembers = new HashSet<>();
            for (int i = 0; i < 20; i++) {
                waitingMembers.add(
                        new WaitingMember(
                                "email" + i + "@email.com", performanceId, i, ZonedDateTime.now()));
            }
            runningManager.enterRunningRoom(performanceId, waitingMembers);

            // when
            long runningCount = runningManager.getAvailableToRunning(performanceId);

            // then
            assertThat(runningCount).isEqualTo(80);
        }
    }

    @Nested
    @DisplayName("작업 가능 공간 입장 호출 시")
    class EnterRunningRoomTest {

        private Set<WaitingMember> waitingMembers;
        private int waitingMemberCount;
        private long performanceId;

        @BeforeEach
        void setUp() {
            waitingMemberCount = 20;
            performanceId = 1;
            waitingMembers = new HashSet<>();
            for (int i = 0; i < waitingMemberCount; i++) {
                waitingMembers.add(
                        new WaitingMember(
                                "email" + i + "@email.com", performanceId, i, ZonedDateTime.now()));
            }
        }

        @Test
        @DisplayName("입장 인원만큼 작업 가능 공간 이동 인원 카운터를 증가시킨다.")
        void incrementRunningCounter() {
            // given

            // when
            runningManager.enterRunningRoom(performanceId, waitingMembers);

            // then
            long runningCount = runningManager.getRunningCount(performanceId);
            assertThat(runningCount).isEqualTo(waitingMemberCount);
        }

        @Test
        @DisplayName("작업 가능 공간에 사용자를 추가한다.")
        void enterRunningRoom() {
            // given

            // when
            runningManager.enterRunningRoom(performanceId, waitingMembers);

            // then
            Set<String> waitingMembers = room.get(performanceId).keySet();
            assertThat(waitingMembers).hasSize(waitingMemberCount);
        }
    }
}
