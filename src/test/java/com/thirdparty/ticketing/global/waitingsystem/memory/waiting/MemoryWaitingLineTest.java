package com.thirdparty.ticketing.global.waitingsystem.memory.waiting;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.thirdparty.ticketing.domain.waitingsystem.waiting.WaitingMember;

class MemoryWaitingLineTest {

    private MemoryWaitingLine waitingLine;

    @Nested
    @DisplayName("대기열 입장 시")
    class EnterTest {

        private ConcurrentMap<Long, ConcurrentLinkedQueue<WaitingMember>> line;

        @BeforeEach
        void setUp() {
            line = new ConcurrentHashMap<>();
            waitingLine = new MemoryWaitingLine(line);
        }

        @Test
        @DisplayName("사용자를 대기열에 추가한다.")
        void addWaitingLine() {
            // given
            long performanceId = 1;
            long waitingCounter = 1;
            WaitingMember waitingMember = new WaitingMember("email@email.com", performanceId);
            waitingMember.updateWaitingInfo(waitingCounter, ZonedDateTime.now());

            // when
            waitingLine.enter(waitingMember);

            // then
            assertThat(line.get(performanceId).size()).isEqualTo(1);
            assertThat(line.get(performanceId).poll()).isEqualTo(waitingMember);
        }

        @Test
        @DisplayName("사용자를 순차적으로 대기열에 추가한다.")
        void addWaitingLineSequentially() {
            // given
            long performanceId = 1;
            List<WaitingMember> waitingMembers = new ArrayList<>();
            int waitingCounter = 5;
            for (int i = 0; i < waitingCounter; i++) {
                waitingMembers.add(new WaitingMember("email" + i + "@email.com", performanceId));
            }

            // when
            for (int i = 0; i < waitingCounter; i++) {
                WaitingMember waitingMember = waitingMembers.get(i);
                waitingMember.updateWaitingInfo(i, ZonedDateTime.now());
                waitingLine.enter(waitingMember);
            }

            // then
            List<WaitingMember> result = new ArrayList<>();
            for (int i = 0; i < waitingCounter; i++) {
                result.add(line.get(performanceId).poll());
            }
            assertThat(result.size()).isEqualTo(waitingCounter);
            assertThat(result).isEqualTo(waitingMembers);
        }

        @Test
        @DisplayName("서로 다른 공연은 같은 대기열을 공유하지 않는다.")
        void notSharedWaitingLine() {
            // given
            long performanceAId = 1;
            int performanceAWaitedMemberCount = 5;
            long performanceBId = 2;
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
            List<WaitingMember> resultA = new ArrayList<>();
            List<WaitingMember> resultB = new ArrayList<>();
            for (int i = 0; i < performanceAWaitedMemberCount; i++) {
                resultA.add(line.get(performanceAId).poll());
            }
            for (int i = 0; i < performanceBWaitedMemberCount; i++) {
                resultB.add(line.get(performanceBId).poll());
            }
            assertThat(resultA).isNotEqualTo(resultB);
            assertThat(resultA.size()).isEqualTo(performanceAWaitedMemberCount);
            assertThat(resultB.size()).isEqualTo(performanceBWaitedMemberCount);
        }
    }
}
