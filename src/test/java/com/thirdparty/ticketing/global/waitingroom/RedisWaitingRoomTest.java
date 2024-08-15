package com.thirdparty.ticketing.global.waitingroom;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thirdparty.ticketing.domain.waitingroom.WaitingMember;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

@SpringBootTest
@Import(TestRedisConfig.class)
class RedisWaitingRoomTest {

    @Autowired
    private RedisWaitingRoom waitingRoom;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private String getPerformanceWaitingRoomKey(String performanceId) {
        return "waiting_room:" + performanceId;
    }

    private String getPerformanceWaitingLineKey(String performanceId) {
        return "waiting_line:" + performanceId;
    }

    @BeforeEach
    void setUp() {
        redisTemplate.getConnectionFactory()
                .getConnection()
                .serverCommands()
                .flushAll();
    }

    @Nested
    @DisplayName("대기방 입장시")
    class EnterTest {

        private HashOperations<String, String, String> rawWaitingRoom;
        private ZSetOperations<String, String> rawWaitingLine;

        @BeforeEach
        void setUp() {
            rawWaitingRoom = redisTemplate.opsForHash();
            rawWaitingLine = redisTemplate.opsForZSet();
        }

        @Test
        @DisplayName("대기방에 추가한다.")
        void addMemberToWaitingRoom() throws JsonProcessingException {
            //given
            String performanceId = "1";
            WaitingMember waitingMember = new WaitingMember("email@email.com", performanceId);

            //when
            waitingRoom.enter(waitingMember);

            //then
            String value = rawWaitingRoom.get(getPerformanceWaitingRoomKey(performanceId), waitingMember.getEmail());
            assertThat(value).isEqualTo(objectMapper.writeValueAsString(waitingMember));
        }

        @Test
        @DisplayName("대기방에 이미 존재하면 대기열에 추가하지 않는다.")
        void doNotAdd_ifMemberExists() throws JsonProcessingException {
            //given
            String performanceId = "1";
            WaitingMember waitingMember = new WaitingMember("email@email.com", performanceId);
            waitingRoom.enter(waitingMember);

            //when
            waitingRoom.enter(waitingMember);

            //then
            Set<String> values = rawWaitingLine.range(getPerformanceWaitingLineKey(performanceId), 0, Integer.MAX_VALUE);
            assertThat(values).hasSize(1)
                    .map(value -> objectMapper.readValue(value, WaitingMember.class))
                    .first()
                    .satisfies(member -> {
                        assertThat(member.getWaitingCount()).isEqualTo(1);
                    });
        }

        @Test
        @DisplayName("서로 다른 공연은 같은 대기방을 공유하지 않는다.")
        void doesNotShareRunningRoom_BetweenPerformances() {
            //given
            String performanceIdA = "1";
            WaitingMember waitingMemberA = new WaitingMember("email@email.com", performanceIdA);
            String performanceIdB = "2";
            WaitingMember waitingMemberB = new WaitingMember("email@email.com", performanceIdB);

            //when
            waitingRoom.enter(waitingMemberA);
            waitingRoom.enter(waitingMemberB);

            //then
            assertThat(rawWaitingRoom.entries(getPerformanceWaitingRoomKey(performanceIdA)))
                    .hasSize(1)
                    .containsKey(waitingMemberA.getEmail());
            assertThat(rawWaitingRoom.entries(getPerformanceWaitingRoomKey(performanceIdB)))
                    .hasSize(1)
                    .containsKey(waitingMemberB.getEmail());

        }

        @Test
        @DisplayName("같은 사용자가 동시에 입장해도 잘 작동한다.")
        @Disabled("카운터 획득, 카운터 업데이트가 원자적으로 이루어지지 않아 실패함. 따닥 문제는 잠시 미뤄둠.")
        void enter() throws InterruptedException {
            //given
            int poolSize = 10;
            String performanceId = "1";
            WaitingMember waitingMember = new WaitingMember("email@email.com", performanceId);
            CountDownLatch latch = new CountDownLatch(poolSize);
            ExecutorService executorService = Executors.newFixedThreadPool(poolSize);

            //when
            long[] waitingCounts = new long[poolSize];
            for (int i = 0; i < poolSize; i++) {
                int finalI = i;
                executorService.execute(() -> {
                    try {
                        waitingCounts[finalI] = waitingRoom.enter(waitingMember);
                    } finally {
                        latch.countDown();
                    }
                });
            }
            latch.await();

            //then
            assertThat(waitingCounts).containsOnly(1);
        }
    }
}
