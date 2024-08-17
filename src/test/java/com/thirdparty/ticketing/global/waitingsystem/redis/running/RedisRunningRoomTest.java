package com.thirdparty.ticketing.global.waitingsystem.redis.running;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.thirdparty.ticketing.global.waitingsystem.redis.TestRedisConfig;
import com.thirdparty.ticketing.support.TestContainerStarter;

@SpringBootTest
@Import(TestRedisConfig.class)
class RedisRunningRoomTest extends TestContainerStarter {

    @Autowired private RedisRunningRoom runningRoom;

    @Autowired private StringRedisTemplate redisTemplate;

    @BeforeEach
    void setUp() {
        redisTemplate.getConnectionFactory().getConnection().serverCommands().flushAll();
    }

    private String getRunningRoomKey(long performanceId) {
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
            long performanceId = 1;
            String email = "email@email.com";
            rawRunningRoom.add(getRunningRoomKey(performanceId), email);

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
            rawRunningRoom.add(getRunningRoomKey(performanceIdA), email);

            // when
            boolean contains = runningRoom.contains(email, performanceIdB);

            // then
            assertThat(contains).isFalse();
        }
    }
}
