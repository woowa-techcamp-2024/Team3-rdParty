package com.thirdparty.ticketing.global.waitingsystem.redis.running;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;
import static org.assertj.core.api.Assertions.within;

import com.thirdparty.ticketing.domain.waitingsystem.waiting.WaitingMember;
import com.thirdparty.ticketing.support.BaseIntegrationTest;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

class RedisRunningRoomTest extends BaseIntegrationTest {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private RedisRunningRoom runningRoom;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private ZSetOperations<String, String> rawRunningRoom;

    @BeforeEach
    void setUp() {
        rawRunningRoom = redisTemplate.opsForZSet();
        redisTemplate.getConnectionFactory().getConnection().serverCommands().flushAll();
    }

    private String getRunningRoomKey(long performanceId) {
        return "running_room:" + performanceId;
    }

    @Nested
    @DisplayName("러닝룸에 사용자가 있는지 확인했을 때")
    class ContainsTest {

        @Test
        @DisplayName("사용자가 포함되어 있다면 true를 반환한다.")
        void true_WhenMemberContains() {
            // given
            long performanceId = 1;
            String email = "email@email.com";
            rawRunningRoom.add(
                    getRunningRoomKey(performanceId), email, ZonedDateTime.now().toEpochSecond());

            // when
            boolean contains = runningRoom.contains(email, performanceId);

            // then
            assertThat(contains).isTrue();
        }

        @Test
        @DisplayName("사용자가 포함되어 있지 않다면 false를 반환한다.")
        void false_WhenMemberDoesNotContain() {
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
            rawRunningRoom.add(
                    getRunningRoomKey(performanceIdA), email, ZonedDateTime.now().toEpochSecond());

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
        @DisplayName("남아있는 자리를 반환한다.")
        void getAvailableToRunning(int runningMembers, int expectedAvailableToRunning) {
            // given
            long performanceId = 1;
            for (int i = 0; i < runningMembers; i++) {
                rawRunningRoom.add(
                        getRunningRoomKey(performanceId),
                        "email" + i + "@email.com",
                        ZonedDateTime.now().toEpochSecond());
            }

            // when
            long availableToRunning = runningRoom.getAvailableToRunning(performanceId);

            // then
            assertThat(availableToRunning).isEqualTo(expectedAvailableToRunning);
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
            Set<WaitingMember> waitingMembers = new HashSet<>();
            for (int i = 0; i < 10; i++) {
                waitingMembers.add(
                        new WaitingMember(
                                "email" + i + "@email.com", performanceId, i, ZonedDateTime.now()));
            }

            // when
            runningRoom.enter(performanceId, waitingMembers);

            // then
            String[] emails =
                    waitingMembers.stream().map(WaitingMember::getEmail).toArray(String[]::new);
            List<Double> score = rawRunningRoom.score(getRunningRoomKey(performanceId), emails);
            assertThat(score).allSatisfy(value -> assertThat(value).isNotNull());
        }

        @Test
        @DisplayName("최초 입장 시 사용자의 작업 만료 시간은 30초이다.")
        void test() {
            //given
            long performanceId = 1;
            String email = "email@email.com";
            Set<WaitingMember> waitingMembers = Set.of(
                    new WaitingMember(email, performanceId, 1, ZonedDateTime.now()));

            //when
            runningRoom.enter(performanceId, waitingMembers);

            //then
            Double score = rawRunningRoom.score(getRunningRoomKey(performanceId), email);
            ZonedDateTime zonedDateTime = ZonedDateTime.of(
                    LocalDateTime.ofEpochSecond(score.longValue(), 0, ZoneOffset.of("+09:00")),
                    ZoneId.of("Asia/Seoul"));
            assertThat(zonedDateTime).isCloseTo(ZonedDateTime.now().plusSeconds(30), within(1, ChronoUnit.SECONDS));
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
            Set<WaitingMember> waitingMembers =
                    Set.of(new WaitingMember(email, performanceId, 1, ZonedDateTime.now()));
            runningRoom.enter(performanceId, waitingMembers);

            // when
            runningRoom.pullOutRunningMember(email, performanceId);

            // then
            assertThat(runningRoom.contains(email, performanceId)).isFalse();
        }

        @Test
        @DisplayName("사용자가 작업 가능 공간에 없으면 무시한다.")
        void ignore_WhenNotExistsMember() {
            // given
            long performanceId = 1;
            String anotherEmail = "email@email.com";
            Set<WaitingMember> waitingMembers =
                    Set.of(new WaitingMember(anotherEmail, performanceId, 1, ZonedDateTime.now()));
            runningRoom.enter(performanceId, waitingMembers);

            String email = "email" + 1 + "@email.com";

            // when
            runningRoom.pullOutRunningMember(email, performanceId);

            // then
            assertThat(runningRoom.contains(email, performanceId)).isFalse();
        }

        @Test
        @DisplayName("작업 가능 공간이 없으면 무시한다.")
        void test() {
            // given
            long performanceId = 1;
            String email = "email@email.com";

            // when
            runningRoom.pullOutRunningMember(email, performanceId);

            // then
            assertThat(runningRoom.contains(email, performanceId)).isFalse();
        }
    }

    @Nested
    @DisplayName("만료된 사용자 제거 호출 시")
    class RemoveExpiredMemberInfoTest {

        @Test
        @DisplayName("만료 시간이 현재 이전인 경우 제거한다.")
        void removeExpiredMemberInfo() {
            // given
            long performanceId = 1;
            String email = "email@email.com";
            long score = ZonedDateTime.now().minusSeconds(1).toEpochSecond();
            rawRunningRoom.add(getRunningRoomKey(performanceId), email, score);

            // when
            runningRoom.removeExpiredMemberInfo(performanceId);

            // then
            assertThat(rawRunningRoom.range(getRunningRoomKey(performanceId), 0, -1)).isEmpty();
        }

        @Test
        @DisplayName("만료 시간이 현재 시간 이후인 사용자 정보는 제거하지 않는다.")
        void notRemoveMemberInfo() {
            // given
            long performanceId = 1;
            String email = "email@email.com";
            long score = ZonedDateTime.now().plusSeconds(10).toEpochSecond();
            rawRunningRoom.add(getRunningRoomKey(performanceId), email, score);

            // when
            runningRoom.removeExpiredMemberInfo(performanceId);

            // then
            assertThat(rawRunningRoom.range(getRunningRoomKey(performanceId), 0, -1))
                    .hasSize(1)
                    .first()
                    .isEqualTo(email);
        }
    }

    @Nested
    @DisplayName("사용자 만료 시간 업데이트 시")
    class UpdateRunningMemberExpiredTimeTest {

        @Test
        @DisplayName("사용자의 만료 시간을 5분으로 업데이트 한다.")
        void updateRunningMemberExpiredTime() {
            //given
            long performanceId = 1;
            String email = "email@email.com";
            runningRoom.enter(performanceId, Set.of(new WaitingMember(email, performanceId)));

            //when
            runningRoom.updateRunningMemberExpiredTime(email, performanceId);

            //then
            assertThat(rawRunningRoom.rangeByScoreWithScores(getRunningRoomKey(performanceId), 0, Double.MAX_VALUE))
                    .hasSize(1)
                    .first()
                    .satisfies(tuple -> {
                        ZonedDateTime memberExpiredAt = ZonedDateTime.ofInstant(
                                Instant.ofEpochSecond(tuple.getScore().longValue()),
                                ZoneId.of("Asia/Seoul"));
                        assertThat(memberExpiredAt).isCloseTo(
                                ZonedDateTime.now().plusMinutes(5),
                                within(1, ChronoUnit.MINUTES));
                    });
        }

        @Test
        @DisplayName("사용자가 작업 공간에 존재하지 않으면 무시한다.")
        void ignore_notExistsMember() {
            //given
            long performanceId = 1;
            String email = "email@email.com";

            //when
            Exception exception = catchException(
                    () -> runningRoom.updateRunningMemberExpiredTime(email, performanceId));

            //then
            assertThat(exception).doesNotThrowAnyException();
            assertThat(rawRunningRoom.zCard(getRunningRoomKey(performanceId))).isZero();
        }
    }
}
