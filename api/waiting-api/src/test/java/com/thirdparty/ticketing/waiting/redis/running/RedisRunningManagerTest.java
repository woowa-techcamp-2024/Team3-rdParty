package com.thirdparty.ticketing.waiting.redis.running;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;

import com.thirdparty.ticketing.testcontainer.RedisTestContainerStarter;
import com.thirdparty.ticketing.waiting.waitingsystem.waiting.WaitingMember;

@SpringBootTest
class RedisRunningManagerTest extends RedisTestContainerStarter {

    @Autowired private RedisRunningManager runningManager;

    @Autowired private StringRedisTemplate redisTemplate;

    private ValueOperations<String, String> rawRunningCounter;
    private ZSetOperations<String, String> rawRunningRoom;

    @BeforeEach
    void setUp() {
        rawRunningCounter = redisTemplate.opsForValue();
        rawRunningRoom = redisTemplate.opsForZSet();
        redisTemplate.getConnectionFactory().getConnection().serverCommands().flushAll();
    }

    private String getRunningCounterKey(long performanceId) {
        return "running_counter:" + performanceId;
    }

    private String getRunningRoomKey(long performanceId) {
        return "running_room:" + performanceId;
    }

    @Nested
    @DisplayName("러닝 카운트 조회 시")
    class GetRunningCountTest {

        @Test
        @DisplayName("작업 가능 공간으로 진입한 인원 수를 반환한다.")
        void getRunningCount() {
            // given
            long performanceId = 1;
            rawRunningCounter.setIfAbsent(getRunningCounterKey(performanceId), "23");

            // when
            long runningCount = runningManager.getRunningCount(performanceId);

            // then
            assertThat(runningCount).isEqualTo(23);
        }

        @Test
        @DisplayName("카운트가 존재하지 않으면 0부터 시작한다.")
        void startCounterWithZeroValue() {
            // given
            long performanceId = 1;

            // when
            long runningCount = runningManager.getRunningCount(performanceId);

            // then
            assertThat(runningCount).isEqualTo(0);
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
            Set<String> waitingMembers =
                    rawRunningRoom.range(getRunningRoomKey(performanceId), 0, -1);
            assertThat(waitingMembers).hasSize(waitingMemberCount);
        }
    }
}
