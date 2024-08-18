package com.thirdparty.ticketing.global.waitingsystem.memory.running;

import static org.assertj.core.api.Assertions.assertThat;

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
}
