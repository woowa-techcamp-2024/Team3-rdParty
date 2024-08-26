package com.thirdparty.ticketing.waiting.memory.waiting;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.thirdparty.ticketing.exception.ErrorCode;
import com.thirdparty.ticketing.exception.TicketingException;
import com.thirdparty.ticketing.waiting.waitingsystem.waiting.WaitingMember;

class MemoryWaitingManagerTest {

    private MemoryWaitingManager waitingManager;
    private ConcurrentMap<Long, ConcurrentMap<String, WaitingMember>> rawWaitingRoom =
            new ConcurrentHashMap<>();
    private ConcurrentMap<Long, AtomicLong> rawWaitingCounter = new ConcurrentHashMap<>();
    private ConcurrentMap<Long, ConcurrentLinkedQueue<WaitingMember>> rawWaitingLine =
            new ConcurrentHashMap<>();

    @BeforeEach
    void setUp() {
        waitingManager =
                new MemoryWaitingManager(
                        new MemoryWaitingRoom(rawWaitingRoom),
                        new MemoryWaitingCounter(rawWaitingCounter),
                        new MemoryWaitingLine(rawWaitingLine));
    }

    @Nested
    @DisplayName("웨이팅 룸 입장 메서드 호출 시")
    class EnterWaitingRoomTest {

        @Test
        @DisplayName("대기방에 추가한다.")
        void addMemberToWaitingRoom() {
            // given
            long performanceId = 1;
            String email = "email@email.com";

            // when
            waitingManager.enterWaitingRoom(email, performanceId);

            // then
            WaitingMember result = rawWaitingRoom.get(performanceId).get(email);
            assertThat(result)
                    .satisfies(
                            member -> {
                                assertThat(member).isNotNull();
                                assertThat(member.getEmail()).isEqualTo(email);
                                assertThat(member.getPerformanceId()).isEqualTo(performanceId);
                            });
        }

        @Test
        @DisplayName("대기방에 이미 존재하면 대기열에 추가하지 않는다.")
        void doNotAdd_ifMemberExists() {
            // given
            long performanceId = 1;
            String email = "email@email.com";
            waitingManager.enterWaitingRoom(email, performanceId);

            // when
            waitingManager.enterWaitingRoom(email, performanceId);

            // then
            assertThat(rawWaitingRoom.get(performanceId))
                    .hasSize(1)
                    .containsOnlyKeys(email)
                    .satisfies(
                            room -> {
                                WaitingMember member = room.get(email);
                                assertThat(member)
                                        .isNotNull()
                                        .extracting(
                                                WaitingMember::getEmail,
                                                WaitingMember::getPerformanceId)
                                        .containsExactly(email, performanceId);
                            });
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
            assertThat(rawWaitingRoom)
                    .hasSize(2)
                    .satisfies(
                            rooms -> {
                                assertThat(rooms.get(performanceIdA))
                                        .hasSize(1)
                                        .containsOnlyKeys(email);
                                assertThat(rooms.get(performanceIdB))
                                        .hasSize(1)
                                        .containsOnlyKeys(email);
                            });
        }

        @Test
        @DisplayName("같은 사용자가 동시에 입장해도 대기방에는 한 번만 입장한다.")
        void sameUserManyEnter() throws InterruptedException {
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
            assertThat(rawWaitingRoom)
                    .hasSize(1)
                    .satisfies(
                            rooms -> {
                                assertThat(rooms.get(performanceId))
                                        .hasSize(1)
                                        .containsOnlyKeys(email)
                                        .satisfies(
                                                room -> {
                                                    WaitingMember member = room.get(email);
                                                    assertThat(member)
                                                            .isNotNull()
                                                            .extracting(
                                                                    WaitingMember::getEmail,
                                                                    WaitingMember::getPerformanceId)
                                                            .containsExactly(email, performanceId);
                                                });
                            });
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
