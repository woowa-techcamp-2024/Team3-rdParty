package com.thirdparty.ticketing.global.waitingsystem;

import static org.assertj.core.api.Assertions.assertThat;

import com.thirdparty.ticketing.domain.common.EventPublisher;
import com.thirdparty.ticketing.domain.waitingsystem.WaitingSystem;
import com.thirdparty.ticketing.support.TestContainerStarter;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

@SpringBootTest
class WaitingEventListenerTest extends TestContainerStarter {

    @Autowired
    private WaitingSystem waitingSystem;

    @Autowired
    private EventPublisher eventPublisher;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private SetOperations<String, String> rawRunningRoom;

    @BeforeEach
    void setUp() {
        rawRunningRoom = redisTemplate.opsForSet();
        redisTemplate.getConnectionFactory()
                .getConnection()
                .commands()
                .flushAll();
    }

    @Nested
    @DisplayName("폴링 이벤트 발행 시")
    class PublishPoolingEventTest {

        @Test
        @DisplayName("대기열 사용자 작업 공간 이동 기능을 트리거한다.")
        void moveUserToRunningRoom() {
            //given
            long performanceId = 1;

            String email = "email@email.com";
            waitingSystem.enterWaitingRoom(email, performanceId);

            //when
            waitingSystem.getRemainingCount(email, performanceId);

            //then
            Set<String> members = rawRunningRoom.members("running_room:" + performanceId);
            System.out.println("회원 목록 출력 " + members);
            assertThat(waitingSystem.isReadyToHandle(email, performanceId)).isTrue();
        }
    }
}
