package com.thirdparty.ticketing.global.waitingsystem.redis.running;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.ZonedDateTime;
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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import com.thirdparty.ticketing.domain.waitingsystem.waiting.WaitingMember;
import com.thirdparty.ticketing.support.TestContainerStarter;

@SpringBootTest
class RedisRunningRoomTest extends TestContainerStarter {

    @Autowired private RedisRunningRoom runningRoom;

    @Autowired private StringRedisTemplate redisTemplate;

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
}
