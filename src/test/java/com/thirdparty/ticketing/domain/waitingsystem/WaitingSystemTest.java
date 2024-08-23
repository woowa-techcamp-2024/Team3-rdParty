package com.thirdparty.ticketing.domain.waitingsystem;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.catchException;

import com.thirdparty.ticketing.domain.common.TicketingException;
import com.thirdparty.ticketing.domain.waitingsystem.waiting.WaitingMember;
import java.time.ZonedDateTime;
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
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;

import com.thirdparty.ticketing.domain.waitingsystem.running.RunningManager;
import com.thirdparty.ticketing.domain.waitingsystem.waiting.WaitingManager;
import com.thirdparty.ticketing.support.SpyEventPublisher;
import com.thirdparty.ticketing.support.TestContainerStarter;

@SpringBootTest
class WaitingSystemTest extends TestContainerStarter {

    private WaitingSystem waitingSystem;

    @Autowired private WaitingManager waitingManager;

    @Autowired private RunningManager runningManager;

    private ZSetOperations<String, String> rawRunningRoom;

    private SpyEventPublisher eventPublisher;

    @Autowired private StringRedisTemplate redisTemplate;

    private ValueOperations<String, String> rawRunningCounter;

    @BeforeEach
    void setUp() {
        eventPublisher = new SpyEventPublisher();
        waitingSystem = new WaitingSystem(waitingManager, runningManager, eventPublisher);
        rawRunningRoom = redisTemplate.opsForZSet();
        rawRunningCounter = redisTemplate.opsForValue();
        redisTemplate.getConnectionFactory().getConnection().serverCommands().flushAll();
    }

    private String getRunningRoomKey(long performanceId) {
        return "running_room:" + performanceId;
    }

    private String getRunningCounterKey(long performanceId) {
        return "running_counter:" + performanceId;
    }

    @Nested
    @DisplayName("사용자의 남은 순번 조회 시")
    class GetRemainingCountTest {

        @ParameterizedTest
        @CsvSource({"0, 0, 1", "15, 10, 6"})
        @DisplayName("자신이 몇 번째 차례인지 반환한다.")
        void getRemainingCount(int waitingCount, String runningCount, int expected) {
            // given
            long performanceId = 1;
            for (int i = 0; i < waitingCount; i++) {
                waitingSystem.enterWaitingRoom("email" + i + "@email.com", performanceId);
            }
            String email = "email@email.com";
            waitingSystem.enterWaitingRoom(email, performanceId);
            rawRunningCounter.set(getRunningCounterKey(performanceId), runningCount);

            // when
            long remainingCount = waitingSystem.getRemainingCount(email, performanceId);

            // then
            assertThat(remainingCount).isEqualTo(expected);
        }

        @Test
        @DisplayName("폴링 이벤트를 발행한다.")
        void publishPollingEvent() {
            // given
            long performanceId = 1;
            String email = "email@email.com";
            waitingSystem.enterWaitingRoom(email, performanceId);

            // when
            waitingSystem.getRemainingCount(email, performanceId);

            // then
            assertThat(eventPublisher.counter).hasValue(1);
        }
    }

    @Nested
    @DisplayName("대기열 사용자 작업 가능 공간 이동 호출 시")
    class MoveUserToRunningTest {

        @Test
        @DisplayName("작업 공간의 작업 시간이 만료된 사용자를 제거한다.")
        void removeExpiredMemberInfo() {
            // given
            long performanceId = 1;
            String email = "email@email.com";
            long score = ZonedDateTime.now().minusMinutes(5).toEpochSecond();
            rawRunningRoom.add(getRunningRoomKey(performanceId), email, score);

            // when
            waitingSystem.moveUserToRunning(performanceId);

            // then
            assertThat(runningManager.isReadyToHandle(email, performanceId)).isFalse();
        }

        @Test
        @DisplayName("작업 가능 공간의 수용 가능한 인원이 감소한다.")
        void decrementAvailableCount() {
            // given
            long performanceId = 1;
            int memberCount = 25;
            for (int i = 0; i < memberCount; i++) {
                waitingManager.enterWaitingRoom("email" + i + "@email.com", performanceId);
            }

            // when
            waitingSystem.moveUserToRunning(performanceId);

            // then
            assertThat(runningManager.getRunningCount(performanceId)).isEqualTo(memberCount);
            assertThat(runningManager.getAvailableToRunning(performanceId))
                    .isEqualTo(100 - memberCount);
        }

        @Test
        @DisplayName("더 이상 인원을 수용할 수 없으면 작업 가능 공간에 사용자를 추가하지 않는다.")
        void doNotMoveUserToRunning_WhenNoMoreAvailableSpace() {
            // given
            long performanceId = 1;
            for (int i = 0; i < 100; i++) {
                waitingSystem.enterWaitingRoom("email" + i + "@email.com", performanceId);
            }
            waitingSystem.moveUserToRunning(performanceId);

            int memberCount = 25;
            for (int i = 0; i < memberCount; i++) {
                waitingManager.enterWaitingRoom("email" + i + "@email.com", performanceId);
            }

            // when
            waitingSystem.moveUserToRunning(performanceId);

            // then
            assertThat(runningManager.getRunningCount(performanceId)).isEqualTo(100);
            assertThat(runningManager.getAvailableToRunning(performanceId)).isEqualTo(0);
        }
    }

    @Nested
    @DisplayName("대기중인 사용자 제거 호출 시")
    class PullOutRunningMemberTest {

        @Test
        @DisplayName("대기열 시스템에서 사용자 정보를 제거한다.")
        void pullOutMember() {
            //given
            long performanceId = 1;
            String email = "email@email.com";
            waitingManager.enterWaitingRoom(email, performanceId);
            WaitingMember waitingMember = new WaitingMember(email, performanceId, 1, ZonedDateTime.now());
            runningManager.enterRunningRoom(performanceId, Set.of(waitingMember));

            //when
            waitingSystem.pullOutRunningMember(email, performanceId);

            //then
            assertThat(runningManager.isReadyToHandle(email, performanceId)).isFalse();
            assertThatThrownBy(() -> waitingManager.findWaitingMember(email, performanceId))
                    .isInstanceOf(TicketingException.class);
        }

        @Test
        @DisplayName("사용자 정보가 없으면 무시한다.")
        void ignore_WhenMemberInfoNotExists() {
            //given
            String email = "email@email.com";
            long performanceId = 1;

            //when
            Exception exception = catchException(() -> waitingSystem.pullOutRunningMember(email, performanceId));

            //then
            assertThat(exception).doesNotThrowAnyException();
        }
    }
}
