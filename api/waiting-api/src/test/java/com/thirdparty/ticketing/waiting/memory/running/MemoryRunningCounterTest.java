package com.thirdparty.ticketing.waiting.memory.running;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.ConcurrentHashMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class MemoryRunningCounterTest {

    private MemoryRunningCounter runningCounter;

    @BeforeEach
    void setUp() {
        runningCounter = new MemoryRunningCounter(new ConcurrentHashMap<>());
    }

    @Nested
    @DisplayName("카운터 증가 호출 시")
    class IncrementTest {

        @Test
        @DisplayName("주어진 값만큼 값을 증가시킨다.")
        void increment() {
            // given
            long performanceId = 1;
            int number = 10;

            // when
            runningCounter.increment(performanceId, number);

            // then
            long runningCount = runningCounter.getRunningCounter(performanceId);
            assertThat(runningCount).isEqualTo(number);
        }
    }
}
