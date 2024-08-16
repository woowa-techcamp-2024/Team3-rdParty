package com.thirdparty.ticketing.global.waiting;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thirdparty.ticketing.domain.waitingsystem.WaitingMember;
import com.thirdparty.ticketing.global.waiting.room.RedisWaitingLine;
import com.thirdparty.ticketing.support.TestContainerStarter;

@SpringBootTest
@Disabled("구조 변경으로 더 이상 사용하지 않음")
class RedisWaitingLineTest extends TestContainerStarter {

    private static final String WAITING_LINE_KEY = "waiting_line:";

    private RedisWaitingLine waitingLine;

    @Autowired private ObjectMapper objectMapper;

    @Qualifier("lettuceRedisTemplate")
    @Autowired
    private StringRedisTemplate redisTemplate;

    @BeforeEach
    void setUp() {
        redisTemplate.getConnectionFactory().getConnection().serverCommands().flushAll();
        waitingLine = new RedisWaitingLine(objectMapper, redisTemplate);
    }

    private String getPerformanceWaitingLineKey(String performanceId) {
        return WAITING_LINE_KEY + performanceId;
    }

    @Nested
    @DisplayName("대기열 입장 시")
    class EnterTest {

        private ZSetOperations<String, String> rawWaitingLine;

        @BeforeEach
        void setUp() {
            rawWaitingLine = redisTemplate.opsForZSet();
        }

        @Test
        @DisplayName("사용자를 대기열에 추가한다.")
        void addWaitingLine() throws JsonProcessingException {
            // given
            String performanceId = "1";
            long waitingCounter = 1;
            WaitingMember waitingMember = new WaitingMember("email@email.com", performanceId);
            waitingMember.updateWaitingInfo(waitingCounter, ZonedDateTime.now());

            // when
            waitingLine.enter(waitingMember);

            // then
            String performanceWaitingLineKey = getPerformanceWaitingLineKey(performanceId);
            assertThat(rawWaitingLine.size(performanceWaitingLineKey)).isEqualTo(1);
            assertThat(rawWaitingLine.popMax(performanceWaitingLineKey).getValue())
                    .isEqualTo(objectMapper.writeValueAsString(waitingMember));
        }

        @Test
        @DisplayName("사용자를 순차적으로 대기열에 추가한다.")
        void addWaitingLineSequentially() {
            // given
            String performanceId = "1";
            List<WaitingMember> waitingMembers = new ArrayList<>();
            int waitedMemberCount = 5;
            for (int i = 0; i < waitedMemberCount; i++) {
                waitingMembers.add(new WaitingMember("email" + i + "@email.com", performanceId));
            }

            // when
            for (int i = 0; i < waitedMemberCount; i++) {
                WaitingMember waitingMember = waitingMembers.get(i);
                waitingMember.updateWaitingInfo(i, ZonedDateTime.now());
                waitingLine.enter(waitingMember);
            }

            // then
            List<String> expected =
                    waitingMembers.stream()
                            .map(
                                    member -> {
                                        try {
                                            return objectMapper.writeValueAsString(member);
                                        } catch (JsonProcessingException e) {
                                            throw new RuntimeException(e);
                                        }
                                    })
                            .toList();
            String performanceWaitingLineKey = getPerformanceWaitingLineKey(performanceId);
            Set<String> values =
                    rawWaitingLine.range(performanceWaitingLineKey, 0, Integer.MAX_VALUE);

            assertThat(values).hasSize(waitedMemberCount).containsExactlyElementsOf(expected);
        }

        @Test
        @DisplayName("서로 다른 공연은 같은 대기열을 공유하지 않는다.")
        void notSharedWaitingLine() {
            // given
            String performanceAId = "1";
            int performanceAWaitedMemberCount = 5;
            String performanceBId = "2";
            int performanceBWaitedMemberCount = 10;

            // when
            for (int i = 0; i < performanceAWaitedMemberCount; i++) {
                WaitingMember waitingMember =
                        new WaitingMember("email" + i + "@email.com", performanceAId);
                waitingMember.updateWaitingInfo(i, ZonedDateTime.now());
                waitingLine.enter(waitingMember);
            }

            for (int i = 0; i < performanceBWaitedMemberCount; i++) {
                WaitingMember waitingMember =
                        new WaitingMember("email" + i + "@email.com", performanceBId);
                waitingMember.updateWaitingInfo(i, ZonedDateTime.now());
                waitingLine.enter(waitingMember);
            }

            // then
            Set<String> performanceAWaitedMembers =
                    rawWaitingLine.range(
                            getPerformanceWaitingLineKey(performanceAId), 0, Integer.MAX_VALUE);
            Set<String> performanceBWaitedMembers =
                    rawWaitingLine.range(
                            getPerformanceWaitingLineKey(performanceBId), 0, Integer.MAX_VALUE);

            assertThat(performanceAWaitedMembers)
                    .doesNotContainAnyElementsOf(performanceBWaitedMembers);
            assertThat(performanceAWaitedMembers).hasSize(performanceAWaitedMemberCount);
            assertThat(performanceBWaitedMembers).hasSize(performanceBWaitedMemberCount);
        }
    }
}
