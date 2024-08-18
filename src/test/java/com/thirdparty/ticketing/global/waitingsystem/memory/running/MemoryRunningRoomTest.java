package com.thirdparty.ticketing.global.waitingsystem.memory.running;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import com.thirdparty.ticketing.domain.waitingsystem.waiting.WaitingMember;

class MemoryRunningRoomTest {

    private MemoryRunningRoom runningRoom;
    private ConcurrentMap<Long, ConcurrentMap<String, WaitingMember>> room;

    @BeforeEach
    void setUp() {
        room = new ConcurrentHashMap<>();
        runningRoom = new MemoryRunningRoom(room);
    }

    @Nested
    @DisplayName("러닝 룸에 사용자가 있는지 확인했을 때")
    class ContainTest {

        @Test
        @DisplayName("사용자가 포함되어 있다면 true 를 반환한다.")
        void true_WhenMemberContains() {
            // given
            long performanceId = 1;
            String email = "email@email.com";
            room.putIfAbsent(performanceId, new ConcurrentHashMap<>());
            room.get(performanceId).putIfAbsent(email, new WaitingMember());

            // when
            boolean contains = runningRoom.contains(email, performanceId);

            // then
            assertThat(contains).isTrue();
        }

        @Test
        @DisplayName("사용자가 포함되어 있지 않다면 false 를 반환한다.")
        void false_WhenMemberDoesNotContains() {
            // given
            long performanceId = 1;
            String email = "email@email.com";

            // when
            boolean contains = runningRoom.contains(email, performanceId);

            // then
            assertThat(contains).isFalse();
        }

        @Test
        @DisplayName("서로 다른 공연은 러닝룸을 공유하지 않는다.")
        void doesNotShareRunningRoom_BetweenPerformances() {
            // given
            long performanceIdA = 1;
            long performanceIdB = 2;
            String email = "email@email.com";
            room.putIfAbsent(performanceIdA, new ConcurrentHashMap<>());
            room.get(performanceIdA).putIfAbsent(email, new WaitingMember());

            // when
            boolean contains = runningRoom.contains(email, performanceIdB);

            // then
            assertThat(contains).isFalse();
        }
    }

    @Nested
    @DisplayName("작업 가능 공간에 빈 자리가 있는지 조회 시")
    class GetAvailableToRunningTest {

        @ParameterizedTest
        @CsvSource({"0, 100", "50, 50", "100, 0"})
        @DisplayName("남아 있는 자리를 반환한다.")
        void getAvailableToRunning(int membersCount, int expectAvailableToRunning) {
            // given
            long performanceId = 1;
            room.putIfAbsent(performanceId, new ConcurrentHashMap<>());
            for (int i = 0; i < membersCount; i++) {
                room.get(performanceId)
                        .putIfAbsent("email" + i + "@email.com", new WaitingMember());
            }

            // when
            long availableToRunning = runningRoom.getAvailableToRunning(performanceId);

            // then
            assertThat(availableToRunning).isEqualTo(expectAvailableToRunning);
        }
    }
}
