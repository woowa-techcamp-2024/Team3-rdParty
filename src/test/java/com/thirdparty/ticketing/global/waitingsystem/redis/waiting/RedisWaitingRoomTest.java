package com.thirdparty.ticketing.global.waitingsystem.redis.waiting;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.catchException;

import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thirdparty.ticketing.domain.common.TicketingException;
import com.thirdparty.ticketing.support.BaseIntegrationTest;

class RedisWaitingRoomTest extends BaseIntegrationTest {

    @Autowired private RedisWaitingRoom waitingRoom;

    @Autowired private StringRedisTemplate redisTemplate;

    @Autowired private ObjectMapper objectMapper;

    private HashOperations<String, String, String> rawWaitingRoom;

    private String getWaitingRoomKey(long performanceId) {
        return "waiting_room:" + performanceId;
    }

    @BeforeEach
    void setUp() {
        rawWaitingRoom = redisTemplate.opsForHash();
        redisTemplate.getConnectionFactory().getConnection().serverCommands().flushAll();
    }

    @Nested
    @DisplayName("대기방 입장 메서드 호출 시")
    class EnterTest {

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
            assertThat(rawWaitingRoom.entries(getWaitingRoomKey(performanceId)))
                    .hasSize(1)
                    .containsKey(email);
        }

        @Test
        @DisplayName("사용자가 있으면 대기방에 추가하지 않는다.")
        void test() {
            // given
            long performanceId = 1;
            String email = "email@email.com";
            waitingRoom.enter(email, performanceId);

            // when
            boolean enter = waitingRoom.enter(email, performanceId);

            // then
            assertThat(enter).isFalse();
            assertThat(rawWaitingRoom.entries(getWaitingRoomKey(performanceId))).hasSize(1);
        }
    }

    @Nested
    @DisplayName("대기방 사용자 업데이트 메서드 호출 시")
    class UpdateMemberInfoTest {

        @Test
        @DisplayName("사용자 정보를 업데이트 한다.")
        void updateWaitingMember() {
            // given
            String email = "email@email.com";
            long performanceId = 1;
            long waitingCount = 1;
            boolean enter = waitingRoom.enter(email, performanceId);

            // when
            waitingRoom.updateMemberInfo(email, performanceId, waitingCount);

            // then
            String value = rawWaitingRoom.get(getWaitingRoomKey(performanceId), email);
            assertThat(Optional.ofNullable(value))
                    .isNotEmpty()
                    .get()
                    .satisfies(
                            count -> {
                                assertThat(count).isEqualTo(String.valueOf(waitingCount));
                            });
        }
    }

    @Nested
    @DisplayName("대기방 사용자 정보 제거 호출 시")
    class removeMemberInfoTest {

        @Test
        @DisplayName("샤용자 정보를 제거한다.")
        void removeMemberInfo() {
            // given
            long performanceId = 1;
            String email = "email@email.com";
            waitingRoom.enter(email, performanceId);

            // when
            waitingRoom.removeMemberInfo(email, performanceId);

            // then
            assertThatThrownBy(() -> waitingRoom.getMemberWaitingCount(email, performanceId))
                    .isInstanceOf(TicketingException.class);
        }

        @Test
        @DisplayName("사용자 정보가 존재하지 않으면 무시한다.")
        void ignore_WhenNotExistsMemberInfo() {
            // given
            long performanceId = 1;
            String anotherEmail = "anotherEmail@email.com";
            waitingRoom.enter(anotherEmail, performanceId);

            String email = "email@email.com";

            // when
            waitingRoom.removeMemberInfo(email, performanceId);

            // then
            assertThatThrownBy(() -> waitingRoom.getMemberWaitingCount(email, performanceId))
                    .isInstanceOf(TicketingException.class);
        }

        @Test
        @DisplayName("대기방 정보가 존재하지 않으면 무시한다.")
        void ignore_WhenNotExistsWaitingRoom() {
            // given
            long performanceId = 1;
            String email = "email@email.com";

            // when
            waitingRoom.removeMemberInfo(email, performanceId);

            // then
            assertThatThrownBy(() -> waitingRoom.getMemberWaitingCount(email, performanceId))
                    .isInstanceOf(TicketingException.class);
        }
    }

    @Nested
    @DisplayName("대기방 사용자 정보 목록 제거 호출 시")
    class RemoveMemberInfos {

        @Test
        @DisplayName("사용자 정보가 제거된다.")
        void removeMemberInfo() {
            // given
            long performanceId = 1;
            String email = "email@email.com";
            String email2 = "email2@email.com";
            waitingRoom.enter(email, performanceId);
            waitingRoom.enter(email2, performanceId);

            // when
            waitingRoom.removeMemberInfo(Set.of(email, email2), performanceId);

            // then
            assertThatThrownBy(() -> waitingRoom.getMemberWaitingCount(email, performanceId))
                    .isInstanceOf(TicketingException.class);
            assertThatThrownBy(() -> waitingRoom.getMemberWaitingCount(email2, performanceId))
                    .isInstanceOf(TicketingException.class);
        }

        @Test
        @DisplayName("사용자 정보가 대기방에 존재하지 않으면 무시한다.")
        void ignore_WhenNotExistsWaitingRoom() {
            // given
            long performanceId = 1;
            String email = "email@email.com";

            // when
            Exception exception =
                    catchException(
                            () -> waitingRoom.removeMemberInfo(Set.of(email), performanceId));

            // then
            assertThat(exception).doesNotThrowAnyException();
        }
    }
}
