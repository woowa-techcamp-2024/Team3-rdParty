package com.thirdparty.ticketing.global.waitingsystem.redis.waiting;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thirdparty.ticketing.support.BaseIntegrationTest;

class RedisWaitingLineTest extends BaseIntegrationTest {

    private static final String WAITING_LINE_KEY = "waiting_line:";

    @Autowired private RedisWaitingLine waitingLine;

    @Autowired private ObjectMapper objectMapper;

    @Autowired private StringRedisTemplate redisTemplate;

    @BeforeEach
    void setUp() {
        redisTemplate.getConnectionFactory().getConnection().serverCommands().flushAll();
    }

    private String getWaitingLineKey(long performanceId) {
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
        void addWaitingLine() {
            // given
            long performanceId = 1;
            long waitingCount = 1;
            String email = "email@email.com";

            // when
            waitingLine.enter(email, performanceId, waitingCount);

            // then
            String performanceWaitingLineKey = getWaitingLineKey(performanceId);
            assertThat(rawWaitingLine.size(performanceWaitingLineKey)).isEqualTo(1);
            assertThat(rawWaitingLine.popMax(performanceWaitingLineKey).getValue())
                    .isEqualTo(email);
        }

        @Test
        @DisplayName("사용자를 순차적으로 대기열에 추가한다.")
        void addWaitingLineSequentially() {
            // given
            long performanceId = 1;
            List<String> memberEmails = new ArrayList<>();
            int waitedMemberCount = 5;
            for (int i = 0; i < waitedMemberCount; i++) {
                memberEmails.add("email" + i + "@email.com");
            }

            // when
            for (int i = 0; i < waitedMemberCount; i++) {
                waitingLine.enter(memberEmails.get(i), performanceId, waitedMemberCount);
            }

            // then
            List<String> expected = memberEmails.stream().toList();
            String performanceWaitingLineKey = getWaitingLineKey(performanceId);
            Set<String> values =
                    rawWaitingLine.range(performanceWaitingLineKey, 0, Integer.MAX_VALUE);

            assertThat(values).hasSize(waitedMemberCount).containsExactlyElementsOf(expected);
        }

        @Test
        @DisplayName("서로 다른 공연은 같은 대기열을 공유하지 않는다.")
        void notSharedWaitingLine() {
            // given
            long performanceAId = 1;
            int countA = 5;

            long performanceBId = 2;
            int countB = 10;

            // when
            for (int i = 0; i < countA; i++) {
                String email = "email" + i + "@email.com";
                waitingLine.enter(email, performanceAId, i);
            }

            for (int i = countA; i < countA + countB; i++) {
                String email = "email" + i * 10 + "@email.com";
                waitingLine.enter(email, performanceBId, i);
            }

            // then
            Set<String> performanceAWaitedMembers =
                    rawWaitingLine.range(getWaitingLineKey(performanceAId), 0, Integer.MAX_VALUE);
            Set<String> performanceBWaitedMembers =
                    rawWaitingLine.range(getWaitingLineKey(performanceBId), 0, Integer.MAX_VALUE);

            assertThat(performanceAWaitedMembers)
                    .doesNotContainAnyElementsOf(performanceBWaitedMembers);
            assertThat(performanceAWaitedMembers).hasSize(countA);
            assertThat(performanceBWaitedMembers).hasSize(countB);
        }
    }

    @Nested
    @DisplayName("대기열에서 사용자를 꺼내올 떄")
    class PullOutMembersTest {

        @Test
        @DisplayName("대기 번호가 낮은 순으로 꺼내온다.")
        void pullOutMembers_LowestWaitingCount() {
            // given
            long performanceId = 1;
            int memberCount = 20;
            List<String> waitingMembers = new ArrayList<>();
            for (int i = 1; i <= memberCount; i++) {
                String email = "email" + i + "@email.com";
                waitingLine.enter(email, performanceId, i);
                waitingMembers.add(email);
            }

            // when
            Set<String> emails = waitingLine.pullOutMembers(performanceId, 5);

            // then
            waitingMembers.stream().limit(5);
            assertThat(emails)
                    .containsExactlyInAnyOrderElementsOf(waitingMembers.stream().limit(5).toList());
        }

        @Test
        @DisplayName("꺼내올 인원이 대기열의 인원보다 많은 경우 모든 인원을 꺼내온다.")
        void whenAvailableToRunningIsGraterThanRunningLineSize() {
            // given
            long performanceId = 1;
            int memberCount = 5;
            List<String> waitingMembers = new ArrayList<>();
            for (int i = 1; i <= memberCount; i++) {
                String email = "email" + i + "@email.com";
                waitingLine.enter(email, performanceId, i);
                waitingMembers.add(email);
            }

            // when
            Set<String> emails = waitingLine.pullOutMembers(performanceId, memberCount + 20);

            // then
            assertThat(emails).containsExactlyInAnyOrderElementsOf(waitingMembers);
        }

        @Test
        @DisplayName("대기열에서 꺼낼 인원이 없으면 빈 set을 반환한다.")
        void whenEmpty() {
            // given
            long performanceId = 1;

            // when
            Set<String> emails = waitingLine.pullOutMembers(performanceId, 20);

            // then
            assertThat(emails).isEmpty();
        }
    }
}
