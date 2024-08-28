package com.thirdparty.ticketing.global.waitingsystem.memory.running;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;
import static org.assertj.core.api.Assertions.within;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class MemoryRunningRoomTest {

    private MemoryRunningRoom runningRoom;
    private ConcurrentMap<Long, ConcurrentMap<String, ZonedDateTime>> room;

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
            room.get(performanceId).putIfAbsent(email, ZonedDateTime.now());

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
            room.get(performanceIdA).putIfAbsent(email, ZonedDateTime.now());

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
                        .putIfAbsent("email" + i + "@email.com", ZonedDateTime.now());
            }

            // when
            long availableToRunning = runningRoom.getAvailableToRunning(performanceId);

            // then
            assertThat(availableToRunning).isEqualTo(expectAvailableToRunning);
        }
    }

    @Nested
    @DisplayName("작업 가능 공간 입장 호출 시")
    class EnterTest {

        @Test
        @DisplayName("사용자의 이메일 정보를 작업 가능 공간에 넣는다.")
        void putEmailInfo() {
            // given
            long performanceId = 1;
            Set<String> waitingMembers = new HashSet<>();
            for (int i = 0; i < 10; i++) {
                String email = "email" + i + "@email.com";
                waitingMembers.add(email);
            }

            // when
            runningRoom.enter(performanceId, waitingMembers);

            // then
            String[] emails = waitingMembers.stream().toArray(String[]::new);
            // 각 이메일이 runningRoom 에 존재하는지 확인
            for (String email : emails) {
                assertThat(runningRoom.contains(email, performanceId)).isTrue();
            }
            // 존재하지 않는 이메일은 false 를 반환하는지 확인
            assertThat(runningRoom.contains("nonexistent@email.com", performanceId)).isFalse();
        }
    }

    @Nested
    @DisplayName("작업 가능 공간 사용자 제거 호출 시")
    class PullOutRunningMemberTest {

        @Test
        @DisplayName("사용자를 제거한다.")
        void pullOutRunningMember() {
            // given
            long performanceId = 1;
            String email = "email@email.com";
            runningRoom.enter(performanceId, Set.of(email));
            assertThat(room.get(performanceId)).hasSize(1);

            // when
            runningRoom.pullOutRunningMember(email, performanceId);

            // then
            assertThat(room.get(performanceId)).hasSize(0);
        }

        @Test
        @DisplayName("사용자가 작업 가능 공간에 없으면 무시한다.")
        void ignore_WhenNotExistsMember() {
            // given
            long performanceId = 1;
            String anotherEmail = "email@email.com";
            runningRoom.enter(performanceId, Set.of(anotherEmail));

            String email = "email" + 1 + "@email.com";

            // when
            runningRoom.pullOutRunningMember(email, performanceId);

            // then
            assertThat(room.get(performanceId)).hasSize(1);
        }
    }

    @Nested
    @DisplayName("만료된 사용자 제거 호출 시")
    class RemoveExpiredMemberInfoTest {

        @Test
        @DisplayName("만료 시간이 현재 이전인 사용자는 제거한다.")
        void removeExpiredMemberInfo() {
            // given
            long performanceId = 1;
            String email = "email@email.com";
            ConcurrentHashMap<String, ZonedDateTime> performanceRunning = new ConcurrentHashMap<>();
            performanceRunning.put(email, ZonedDateTime.now().minusMinutes(1));
            room.put(performanceId, performanceRunning);

            // when
            runningRoom.removeExpiredMemberInfo(performanceId);

            // then
            assertThat(room.get(performanceId).get(email)).isNull();
        }

        @Test
        @DisplayName("만료 시간이 현재 시간 이후인 사용자는 제거하지 않는다..")
        void notRemoveMemberInfo() {
            // given
            long performanceId = 1;
            String email = "email@email.com";
            ConcurrentHashMap<String, ZonedDateTime> performanceRunning = new ConcurrentHashMap<>();
            performanceRunning.put(email, ZonedDateTime.now().plusMinutes(1));
            room.put(performanceId, performanceRunning);

            // when
            runningRoom.removeExpiredMemberInfo(performanceId);

            // then
            assertThat(room.get(performanceId).get(email)).isNotNull();
        }
    }

    @Nested
    @DisplayName("사용자 만료 시간 업데이트 시")
    class UpdateRunningMemberExpiredTimeTest {

        @Test
        @DisplayName("사용자의 만료 시간을 5분으로 업데이트 한다.")
        void updateRunningMemberExpiredTime() {
            // given
            long performanceId = 1;
            String email = "email@email.com";
            runningRoom.enter(performanceId, Set.of(email));

            // when
            runningRoom.updateRunningMemberExpiredTime(email, performanceId);

            // then
            ZonedDateTime expiredAt = room.get(performanceId).get(email);
            assertThat(expiredAt)
                    .isCloseTo(ZonedDateTime.now().plusMinutes(5), within(1, ChronoUnit.MINUTES));
        }

        @Test
        @DisplayName("사용자가 작업 공간에 존재하지 않으면 무시한다.")
        void ignore_notExistsMember() {
            // given
            long performanceId = 1;
            String email = "email@email.com";
            room.put(performanceId, new ConcurrentHashMap<>());

            // when
            Exception exception =
                    catchException(
                            () -> runningRoom.updateRunningMemberExpiredTime(email, performanceId));

            // then
            assertThat(exception).doesNotThrowAnyException();
        }
    }
}
