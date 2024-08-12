package com.thirdparty.ticketing.global.security;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.thirdparty.ticketing.domain.member.Member;
import com.thirdparty.ticketing.domain.member.MemberRole;
import com.thirdparty.ticketing.global.security.LoginMemberArgumentResolverTest.ResolverTestController;
import com.thirdparty.ticketing.support.BaseControllerTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@WebMvcTest(controllers = ResolverTestController.class)
class LoginMemberArgumentResolverTest extends BaseControllerTest {

    @RestController
    @RequestMapping("/api/test/resolver")
    public static class ResolverTestController {

        @GetMapping
        public String resolve(@LoginMember String email) {
            return email;
        }
    }

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
            //given

            //when
            ResultActions result = mockMvc.perform(get("/api/test/resolver")
                .header(AUTHORIZATION_HEADER, adminBearerToken));

            //then
            result.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").value(admin.getEmail()));
        }
    }
}