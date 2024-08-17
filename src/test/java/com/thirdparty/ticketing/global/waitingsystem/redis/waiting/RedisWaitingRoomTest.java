package com.thirdparty.ticketing.global.waitingsystem.redis.waiting;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.ZonedDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thirdparty.ticketing.domain.waitingsystem.waiting.WaitingMember;
import com.thirdparty.ticketing.global.waiting.ObjectMapperUtils;
import com.thirdparty.ticketing.global.waitingsystem.redis.TestRedisConfig;
import com.thirdparty.ticketing.support.TestContainerStarter;

@SpringBootTest
@Import(TestRedisConfig.class)
class RedisWaitingRoomTest extends TestContainerStarter {

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
            WaitingMember waitingMember =
                    new WaitingMember(email, performanceId, waitingCount, ZonedDateTime.now());

            // when
            waitingRoom.updateMemberInfo(waitingMember);

            // then
            String value = rawWaitingRoom.get(getWaitingRoomKey(performanceId), email);
            assertThat(Optional.ofNullable(value))
                    .isNotEmpty()
                    .map(v -> ObjectMapperUtils.readValue(objectMapper, v, WaitingMember.class))
                    .get()
                    .satisfies(
                            member -> {
                                assertThat(member.getEmail()).isEqualTo(email);
                                assertThat(member.getPerformanceId()).isEqualTo(performanceId);
                                assertThat(member.getWaitingCount()).isEqualTo(waitingCount);
                            });
        }
    }

    @Nested
    @DisplayName("대기 중인 사용자 조회 시")
    class FindWaitingMemberTest {

        @Test
        @DisplayName("사용자가 존재하면 반환한다.")
        void returnWaitingMember() {
            //given
            String email = "email@email.com";
            long performanceId = 1;
            waitingRoom.enter(email, performanceId);
            waitingRoom.updateMemberInfo(new WaitingMember(email, performanceId, 1, ZonedDateTime.now()));

            //when
            Optional<WaitingMember> optionalWaitingMember = waitingRoom.findWaitingMember(email, performanceId);

            //then
            assertThat(optionalWaitingMember).isNotEmpty()
                    .get()
                    .satisfies(waitingMember -> {
                        assertThat(waitingMember.getEmail()).isEqualTo(email);
                        assertThat(waitingMember.getPerformanceId()).isEqualTo(performanceId);
                    });
        }

        @Test
        @DisplayName("사용자가 존재하지 않으면 빈 값을 반환한다.")
        void returnEmpty() {
            //given
            String email = "email@email.com";
            long performanceId = 1;

            //when
            Optional<WaitingMember> optionalWaitingMember = waitingRoom.findWaitingMember(email, performanceId);

            //then
            assertThat(optionalWaitingMember).isEmpty();
        }
    }
}
