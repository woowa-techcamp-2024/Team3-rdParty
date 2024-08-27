package com.thirdparty.ticketing.global.security;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.thirdparty.ticketing.domain.member.Member;
import com.thirdparty.ticketing.domain.member.MemberRole;
import com.thirdparty.ticketing.support.BaseControllerTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

class LoginMemberArgumentResolverTest extends BaseControllerTest {



    @Nested
    @DisplayName("핸들러 메서드 파라미터에 @LoginMember가 포함되어 있으면")
    class ParameterHasLoginMember {

        private Member admin;
        private String adminBearerToken;

        @BeforeEach
        void setUp() {
            admin = new Member("admin@admin.com", "password", MemberRole.ADMIN);
            adminBearerToken = "Bearer " + jwtProvider.createAccessToken(admin);
        }

        @Test
        @DisplayName("인증 컨텍스트에서 사용자 이메일을 꺼내온다.")
        void getLoginMemberEmail() throws Exception {
            // given

            // when
            ResultActions result =
                    mockMvc.perform(
                            get("/api/test/resolver")
                                    .header(AUTHORIZATION_HEADER, adminBearerToken));

            // then
            result.andExpect(status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$").value(admin.getEmail()));
        }
    }
}
