package com.thirdparty.ticketing.global.waiting;

import static org.assertj.core.api.Assertions.assertThat;

import com.thirdparty.ticketing.domain.waitingroom.WaitingMember;
import com.thirdparty.ticketing.global.waiting.room.RedisWaitingCounter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

@SpringBootTest
class RedisWaitingCounterTest {

    @Autowired
    private RedisWaitingCounter waitingCounter;

    @Qualifier("lettuceRedisTemplate")
    @Autowired
    private StringRedisTemplate redisTemplate;

    @BeforeEach
    void setUp() {
        redisTemplate.getConnectionFactory()
                .getConnection()
                .serverCommands()
                .flushAll();
        waitingCounter = new RedisWaitingCounter(redisTemplate);
    }

    @Nested
    @DisplayName("다음 대기 순번 조회 시")
    class GetNextCountTest {

        private WaitingMember waitingMember;

        @BeforeEach
        void setUp() {
            waitingMember = new WaitingMember("email@email.com", "1");
        }

        @Test
        @DisplayName("순번을 조회한다.")
        void getCount() {
            //given

            //when
            long nextCount = waitingCounter.getNextCount(waitingMember);

            //then
            assertThat(nextCount).isEqualTo(1);
        }

        @Test
        @DisplayName("동시 요청 상황에서 순번을 순차적으로 조회한다.")
        void getCountIncrement() throws InterruptedException {
            //given
            int poolSize = 50;
            CountDownLatch latch = new CountDownLatch(poolSize);
            ExecutorService executorService = Executors.newFixedThreadPool(poolSize);

            List<WaitingMember> waitingMembers = new ArrayList<>();
            for(int i=0; i<poolSize; i++) {
                waitingMembers.add(new WaitingMember("email" + i + "@email.com", "1"));
            }

            //when
            for (int i = 0; i < poolSize; i++) {
                int finalI = i;
                executorService.execute(() -> {
                    try {
                        WaitingMember nextMember = waitingMembers.get(finalI);
                        waitingCounter.getNextCount(nextMember);
                    } finally {
                        latch.countDown();
                    }
                });
            }
            latch.await();

            //then
            assertThat(waitingCounter.getNextCount(waitingMember)).isEqualTo(poolSize + 1);
        }


        @Test
        @DisplayName("각 공연은 대기 순번을 공유하지 않는다.")
        void noSharedWaitingCounter() {
            //given
            String performanceAId = "1";
            int performanceAWaitedMemberCount = 5;
            for(int i = 0; i< performanceAWaitedMemberCount; i++) {
                waitingCounter.getNextCount(new WaitingMember("email" + i + "@email.com", performanceAId));
            }

            String performanceBId = "2";
            int performanceBWaitedMemberCount = 10;
            for(int i = 0; i< performanceBWaitedMemberCount; i++) {
                waitingCounter.getNextCount(new WaitingMember("email" + i + "@email.com", performanceBId));
            }

            //when
            long performanceANextCount = waitingCounter.getNextCount(new WaitingMember("email@email.com", performanceAId));
            long performanceBNextCount = waitingCounter.getNextCount(new WaitingMember("email@email.com", performanceBId));

            //then
            assertThat(performanceANextCount).isNotEqualTo(performanceBNextCount);
            assertThat(performanceANextCount).isEqualTo(performanceAWaitedMemberCount + 1);
            assertThat(performanceBNextCount).isEqualTo(performanceBWaitedMemberCount + 1);
        }
    }
}
