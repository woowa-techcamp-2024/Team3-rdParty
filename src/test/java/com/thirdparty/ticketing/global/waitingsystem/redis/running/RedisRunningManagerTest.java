package com.thirdparty.ticketing.global.waitingsystem.redis.running;

import static org.assertj.core.api.Assertions.assertThat;

import com.thirdparty.ticketing.global.waitingsystem.redis.TestRedisConfig;
import com.thirdparty.ticketing.support.TestContainerStarter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

@SpringBootTest
@Import(TestRedisConfig.class)
class RedisRunningManagerTest extends TestContainerStarter {

    @Autowired
    private RedisRunningManager runningManager;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private ValueOperations<String, String> rawRunningCounter;

    @BeforeEach
    void setUp() {
        rawRunningCounter = redisTemplate.opsForValue();
        redisTemplate.getConnectionFactory()
                .getConnection()
                .serverCommands()
                .flushAll();
    }

    private String getRunningCounterKey(long performanceId) {
        return "running_counter:" + performanceId;
    }

    @Nested
    @DisplayName("러닝 카운트 조회 시")
    class GetRunningCountTest {

        @Test
        @DisplayName("작업 가능 공간으로 진입한 인원 수를 반환한다.")
        void getRunningCount() {
            //given
            long performanceId = 1;
            rawRunningCounter.setIfAbsent(getRunningCounterKey(performanceId), "23");

            //when
            long runningCount = runningManager.getRunningCount(performanceId);

            //then
            assertThat(runningCount).isEqualTo(23);
        }

        @Test
        @DisplayName("카운트가 존재하지 않으면 0부터 시작한다.")
        void startCounterWithZeroValue() {
            //given
            long performanceId = 1;

            //when
            long runningCount = runningManager.getRunningCount(performanceId);

            //then
            assertThat(runningCount).isEqualTo(0);
        }
    }
}
