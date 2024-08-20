package com.thirdparty.ticketing.domain.waitingsystem;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.ZonedDateTime;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thirdparty.ticketing.domain.member.Member;
import com.thirdparty.ticketing.domain.member.MemberRole;
import com.thirdparty.ticketing.domain.member.service.JwtProvider;
import com.thirdparty.ticketing.domain.waitingsystem.WaitingAspectTest.TestController;
import com.thirdparty.ticketing.domain.waitingsystem.waiting.WaitingMember;
import com.thirdparty.ticketing.global.waitingsystem.redis.running.RedisRunningRoom;
import com.thirdparty.ticketing.global.waitingsystem.redis.waiting.RedisWaitingLine;
import com.thirdparty.ticketing.support.TestContainerStarter;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestController.class)
class WaitingAspectTest extends TestContainerStarter {

    private static final String AUTHORIZATION_HEADER = "Authorization";

    @Autowired private JwtProvider jwtProvider;

    @Autowired private MockMvc mockMvc;

    @Autowired private StringRedisTemplate redisTemplate;

    @Autowired private RedisRunningRoom runningRoom;

    @Autowired private RedisWaitingLine waitingLine;

    @RestController
    static class TestController {

        @Waiting
        @GetMapping("/api/waiting/test")
        public ResponseEntity<String> test() {
            return ResponseEntity.ok("test");
        }
    }

    @BeforeEach
    void setUp() {
        redisTemplate.getConnectionFactory().getConnection().commands().flushAll();
    }

    private String getBearerToken(Member member) {
        return "Bearer " + jwtProvider.createAccessToken(member);
    }

    @Nested
    @DisplayName("대기열 시스템 AOP 적용 시")
    class WaitingSystemAop {

        @Nested
        @DisplayName("사용자가 작업 가능 공간에 없으면")
        class WhenRunningRoomNotContainsMember {

            private Member member;
            private String bearerToken;
            private long performanceId;

            @BeforeEach
            void setUp() {
                member =
                        Member.builder()
                                .email("email@email.com")
                                .password("asdfasdf")
                                .memberRole(MemberRole.USER)
                                .build();
                bearerToken = getBearerToken(member);
                performanceId = 1;
            }

            @Test
            @DisplayName("리다이렉트한다.")
            void redirect() throws Exception {
                // given

                // when
                ResultActions result =
                        mockMvc.perform(
                                get("/api/waiting/test")
                                        .header("performanceId", performanceId)
                                        .header(AUTHORIZATION_HEADER, bearerToken));

                // then
                result.andExpect(status().isTemporaryRedirect());
            }

            @Test
            @DisplayName("대기열에 추가한다.")
            void enterWaitingLine() throws Exception {
                // given

                // when
                ResultActions result =
                        mockMvc.perform(
                                get("/api/waiting/test")
                                        .header("performanceId", performanceId)
                                        .header(AUTHORIZATION_HEADER, bearerToken));

                // then
                assertThat(waitingLine.pullOutMembers(performanceId, 1))
                        .map(WaitingMember::getEmail)
                        .hasSize(1)
                        .first()
                        .satisfies(email -> assertThat(email).isEqualTo(member.getEmail()));
            }
        }

        @Test
        @DisplayName("사용자가 작업 가능 공간에 있으면 본래의 기능을 실행한다.")
        void proceed_WhenRunningRoomContainsMember() throws Exception {
            // given
            long performanceId = 1;
            Member member =
                    Member.builder()
                            .email("email@email.com")
                            .password("asdfasdf")
                            .memberRole(MemberRole.USER)
                            .build();
            String bearerToken = getBearerToken(member);
            runningRoom.enter(
                    performanceId,
                    Set.of(
                            new WaitingMember(
                                    member.getEmail(), performanceId, 1, ZonedDateTime.now())));

            // when
            ResultActions result =
                    mockMvc.perform(
                            get("/api/waiting/test")
                                    .header("performanceId", performanceId)
                                    .header(AUTHORIZATION_HEADER, bearerToken));

            // then
            result.andExpect(status().isOk());
        }
    }
}
