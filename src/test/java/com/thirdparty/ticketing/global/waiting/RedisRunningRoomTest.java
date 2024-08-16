package com.thirdparty.ticketing.global.waiting;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.thirdparty.ticketing.domain.waitingsystem.WaitingMember;
import com.thirdparty.ticketing.global.waiting.room.RedisRunningRoom;
import com.thirdparty.ticketing.support.TestContainerStarter;

@SpringBootTest
class RedisRunningRoomTest extends TestContainerStarter {

    @Autowired private RedisRunningRoom runningRoom;

    @Qualifier("lettuceRedisTemplate")
    @Autowired
    private StringRedisTemplate redisTemplate;

    @BeforeEach
    void setUp() {
        redisTemplate.getConnectionFactory().getConnection().serverCommands().flushAll();
    }

    private String getPerformanceRunningRoomKey(String performanceId) {
        return "running_room:" + performanceId;
    }

    @Nested
    @DisplayName("러닝룸에 사용자가 있는지 확인했을 때")
    class ContainsTest {

        private SetOperations<String, String> rawRunningRoom;

        @BeforeEach
        void setUp() {
            rawRunningRoom = redisTemplate.opsForSet();
        }

        @Test
        @DisplayName("사용자가 포함되어 있다면 true를 반환한다.")
        void true_WhenMemberContains() {
            // given
            String performanceId = "1";
            WaitingMember waitingMember = new WaitingMember("email@email.com", performanceId);
            rawRunningRoom.add(
                    getPerformanceRunningRoomKey(performanceId), waitingMember.getEmail());

            // when
            boolean contains = runningRoom.contains(waitingMember);

            // then
            assertThat(contains).isTrue();
        }

        @Test
        @DisplayName("사용자가 포함되어 있지 않다면 false를 반환한다.")
        void false_WhenMemberDoesNotContain() {
            // given
            String performanceId = "1";
            WaitingMember waitingMember = new WaitingMember("email@email.com", performanceId);

            // when
            boolean contains = runningRoom.contains(waitingMember);

            // then
            assertThat(contains).isFalse();
        }

        @Test
        @DisplayName("서로 다른 공연은 러닝룸을 공유하지 않는다.")
        void doesNotShareRunningRoom_BetweenPerformances() {
            // given
            String performanceIdA = "1";
            String performanceIdB = "2";
            String email = "email@email.com";
            WaitingMember waitingMemberA = new WaitingMember(email, performanceIdA);
            rawRunningRoom.add(
                    getPerformanceRunningRoomKey(performanceIdA), waitingMemberA.getEmail());

            WaitingMember waitingMemberB = new WaitingMember(email, performanceIdB);

            // when
            boolean contains = runningRoom.contains(waitingMemberB);

            // then
            assertThat(contains).isFalse();
        }
    }
}
