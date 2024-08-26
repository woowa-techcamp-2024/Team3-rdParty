package com.thirdparty.ticketing.waiting.redis.waiting;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thirdparty.ticketing.exception.ErrorCode;
import com.thirdparty.ticketing.exception.TicketingException;
import com.thirdparty.ticketing.testcontainer.RedisTestContainerStarter;
import com.thirdparty.ticketing.waiting.util.ObjectMapperUtils;
import com.thirdparty.ticketing.waiting.waitingsystem.waiting.WaitingMember;

@SpringBootTest
class RedisWaitingManagerTest extends RedisTestContainerStarter {

    @Autowired private RedisWaitingManager waitingManager;

    @Autowired private StringRedisTemplate redisTemplate;

    @Autowired private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        redisTemplate.getConnectionFactory().getConnection().serverCommands().flushAll();
    }

    private String getWaitingRoomKey(long performanceId) {
        return "waiting_room:" + performanceId;
    }

    private String getWaitingLineKey(long performanceId) {
        return "waiting_line:" + performanceId;
    }

    @Nested
    @DisplayName("웨이팅 룸 입장 메서드 호출 시")
    class EnterWaitingRoomTest {

        private HashOperations<String, String, String> rawWaitingRoom;
        private ZSetOperations<String, String> rawWaitingLine;

        @BeforeEach
        void setUp() {
            rawWaitingRoom = redisTemplate.opsForHash();
            rawWaitingLine = redisTemplate.opsForZSet();
        }

        @Test
        @DisplayName("대기방에 추가한다.")
        void addMemberToWaitingRoom() {
            // given
            long performanceId = 1;
            String email = "email@email.com";

            // when
            waitingManager.enterWaitingRoom(email, performanceId);

            // then
            String value = rawWaitingRoom.get(getWaitingRoomKey(performanceId), email);
            assertThat(Optional.ofNullable(value))
                    .isNotEmpty()
                    .map(v -> ObjectMapperUtils.readValue(objectMapper, v, WaitingMember.class))
                    .get()
                    .satisfies(
                            member -> {
                                assertThat(member.getPerformanceId()).isEqualTo(performanceId);
                                assertThat(member.getEmail()).isEqualTo(email);
                            });
        }

        @Test
        @DisplayName("대기방에 이미 존재하면 대기열에 추가하지 않는다.")
        void doNotAdd_ifMemberExists() throws JsonProcessingException {
            // given
            long performanceId = 1;
            String email = "email@email.com";
            waitingManager.enterWaitingRoom(email, performanceId);

            // when
            waitingManager.enterWaitingRoom(email, performanceId);

            // then
            Set<String> values =
                    rawWaitingLine.range(getWaitingLineKey(performanceId), 0, Integer.MAX_VALUE);
            assertThat(values)
                    .hasSize(1)
                    .map(value -> objectMapper.readValue(value, WaitingMember.class))
                    .first()
                    .satisfies(member -> assertThat(member.getWaitingCount()).isEqualTo(1));
        }

        @Test
        @DisplayName("서로 다른 공연은 같은 대기방을 공유하지 않는다.")
        void doesNotShareRunningRoom_BetweenPerformances() {
            // given
            long performanceIdA = 1;
            long performanceIdB = 2;
            String email = "email@email.com";

            // when
            waitingManager.enterWaitingRoom(email, performanceIdA);
            waitingManager.enterWaitingRoom(email, performanceIdB);

            // then
            assertThat(rawWaitingRoom.entries(getWaitingRoomKey(performanceIdA)))
                    .hasSize(1)
                    .containsKey(email);
            assertThat(rawWaitingRoom.entries(getWaitingRoomKey(performanceIdB)))
                    .hasSize(1)
                    .containsKey(email);
        }

        @Test
        @DisplayName("같은 사용자가 동시에 입장해도 대기방에는 한 번만 입장한다.")
        void enter() throws InterruptedException {
            // given
            int poolSize = 10;
            long performanceId = 1;
            String email = "email@email.com";
            CountDownLatch latch = new CountDownLatch(poolSize);
            ExecutorService executorService = Executors.newFixedThreadPool(poolSize);

            // when
            for (int i = 0; i < poolSize; i++) {
                executorService.execute(
                        () -> {
                            try {
                                waitingManager.enterWaitingRoom(email, performanceId);
                            } finally {
                                latch.countDown();
                            }
                        });
            }
            latch.await();

            // then
            assertThat(rawWaitingRoom.entries(getWaitingRoomKey(performanceId))).hasSize(1);
        }
    }

    @Nested
    @DisplayName("대기중인 사용자 조회 시")
    class FindWaitingMemberTest {

        @Test
        @DisplayName("사용자가 존재하면 반환한다.")
        void returnWaitingMember() {
            // given
            long performanceId = 1;
            String email = "email@email.com";
            waitingManager.enterWaitingRoom(email, performanceId);

            // when
            WaitingMember waitingMember = waitingManager.findWaitingMember(email, performanceId);

            // then
            assertThat(waitingMember.getEmail()).isEqualTo(email);
            assertThat(waitingMember.getPerformanceId()).isEqualTo(performanceId);
        }

        @Test
        @DisplayName("예외(NOT_FOUND_WAITING_MEMBER): 사용자가 존재하지 않으면")
        void notFoundWaitingMember() {
            // given
            long performanceId = 1;
            String email = "email@email.com";

            // when
            Exception exception =
                    catchException(() -> waitingManager.findWaitingMember(email, performanceId));

            // then
            assertThat(exception)
                    .isInstanceOf(TicketingException.class)
                    .extracting(e -> ((TicketingException) e).getErrorCode())
                    .satisfies(
                            errorCode -> {
                                assertThat(errorCode).isEqualTo(ErrorCode.NOT_FOUND_WAITING_MEMBER);
                            });
        }
    }
}
