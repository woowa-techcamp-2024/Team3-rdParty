package com.thirdparty.ticketing.global.waitingsystem.memory.waiting;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.ZonedDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.thirdparty.ticketing.domain.waitingsystem.waiting.WaitingMember;

class MemoryWaitingRoomTest {

    private MemoryWaitingRoom waitingRoom;

    @Nested
    @DisplayName("대기방 입장 메서드 호출 시")
    class EnterTest {

        private ConcurrentMap<Long, ConcurrentMap<String, WaitingMember>> room;

        @BeforeEach
        void setUp() {
            room = new ConcurrentHashMap<>();
            waitingRoom = new MemoryWaitingRoom(room);
        }

        @Test
        @DisplayName("사용자가 없으면 대기방에 추가한다.")
        void enterIfAbsent() {
            // given
            long performanceId = 1;
            String email = "email@email.com";

            // when
            boolean enter = waitingRoom.enter(email, performanceId);

            // then
            assertThat(enter).isTrue();
            assertThat(room.get(performanceId)).hasSize(1).containsKey(email);
        }

        @Test
        @DisplayName("사용자가 있으면 대기방에 추가하지 않는다.")
        void notEnterIfExist() {
            // given
            long performanceId = 1;
            String email = "email@email.com";
            room.computeIfAbsent(performanceId, k -> new ConcurrentHashMap<>())
                    .putIfAbsent(email, new WaitingMember(email, performanceId));

            // when
            boolean enter = waitingRoom.enter(email, performanceId);

            // then
            assertThat(enter).isFalse();
            assertThat(room.get(performanceId)).hasSize(1).containsKey(email);
        }
    }

    @Nested
    @DisplayName("대기방 사용자 업데이트 메서드 호출 시")
    class UpdateMemberInfoTest {

        private ConcurrentMap<Long, ConcurrentMap<String, WaitingMember>> room;

        @BeforeEach
        void setUp() {
            room = new ConcurrentHashMap<>();
            waitingRoom = new MemoryWaitingRoom(room);
        }

        @Test
        @DisplayName("사용자 정보를 업데이트 한다.")
        void updateWaitingMemberInfo() {
            // given
            String email = "email@email.com";
            long performanceId = 1;
            long waitingCount = 1;
            boolean enter = waitingRoom.enter(email, performanceId);
            WaitingMember waitingMember =
                    new WaitingMember(email, performanceId, waitingCount, ZonedDateTime.now());

            // then
            waitingRoom.updateMemberInfo(waitingMember);

            // then
            WaitingMember result = room.get(performanceId).get(email);
            assertThat(result).isEqualTo(waitingMember);
        }
    }
}
