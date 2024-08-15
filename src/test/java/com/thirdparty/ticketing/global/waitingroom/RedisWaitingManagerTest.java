package com.thirdparty.ticketing.global.waitingroom;

import static org.assertj.core.api.Assertions.assertThat;

import com.thirdparty.ticketing.domain.waitingroom.WaitingMember;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

@SpringBootTest
@Import(TestRedisConfig.class)
class RedisWaitingManagerTest {

    @Autowired
    private RedisWaitingManager waitingManager;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @BeforeEach
    void setUp() {
        redisTemplate.getConnectionFactory()
                .getConnection()
                .serverCommands()
                .flushAll();
    }

    @Nested
    @DisplayName("대기방 입장 메서드 호출 시")
    class EnterWaitingRoomTest {

        private ValueOperations<String, String> managedMemberCounter;

        @BeforeEach
        void setUp() {
            managedMemberCounter = redisTemplate.opsForValue();
        }

        private String getPerformanceManagedMemberCounterKey(String performanceId) {
            return "managed_member_counter:" + performanceId;
        }

        @Test
        @DisplayName("사용자의 남은 순번을 반환한다.")
        void addMemberToWaitingLine() {
            //given
            String performanceId = "1";
            String key = getPerformanceManagedMemberCounterKey(performanceId);
            for(int i=0; i<25; i++) {
                WaitingMember waitingMember = new WaitingMember("email" + i + "@email.com", performanceId);
                waitingManager.enterWaitingRoom(waitingMember);
            }
            managedMemberCounter.set(key, "21");

            //when
            long remainingCount = waitingManager.enterWaitingRoom(new WaitingMember("email@email.com", performanceId));

            //then
            assertThat(remainingCount).isEqualTo(5);
        }

        @Test
        @DisplayName("서로 다른 공연은 공연 순번 상태를 공유하지 않는다.")
        void doesNoShared_BetweenPerformances() {
            //given
            String performanceIdA = "1";
            for(int i=0; i<10; i++) {
                WaitingMember waitingMember = new WaitingMember("email" + i + "@email.com", performanceIdA);
                waitingManager.enterWaitingRoom(waitingMember);
            }
            String keyA = getPerformanceManagedMemberCounterKey(performanceIdA);
            managedMemberCounter.set(keyA, "5");

            String performanceIdB = "2";
            for(int i=0; i<5; i++) {
                WaitingMember waitingMember = new WaitingMember("email" + i + "@email.com", performanceIdB);
                waitingManager.enterWaitingRoom(waitingMember);
            }
            String keyB = getPerformanceManagedMemberCounterKey(performanceIdB);
            managedMemberCounter.set(keyB, "2");

            //when
            long remainingCountA = waitingManager.enterWaitingRoom(new WaitingMember("email@email.com", performanceIdA));
            long remainingCountB = waitingManager.enterWaitingRoom(new WaitingMember("email@email.com", performanceIdB));

            //then
            assertThat(remainingCountA).isEqualTo(6);
            assertThat(remainingCountB).isEqualTo(4);
        }
    }
}
