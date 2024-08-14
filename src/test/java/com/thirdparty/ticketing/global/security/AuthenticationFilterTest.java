package com.thirdparty.ticketing.global.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import com.thirdparty.ticketing.domain.common.ErrorCode;
import com.thirdparty.ticketing.domain.common.TicketingException;
import com.thirdparty.ticketing.domain.member.Member;
import com.thirdparty.ticketing.domain.member.MemberRole;
import com.thirdparty.ticketing.domain.member.dto.response.CustomClaims;
import com.thirdparty.ticketing.domain.member.service.JwtProvider;

class AuthenticationFilterTest {

    private AuthenticationFilter authenticationFilter;
    private JwtProvider jwtProvider;
    private MockFilterChain filterChain;

    @BeforeEach
    void setUp() {
        jwtProvider = new JJwtProvider("test", 3600, "thisisjusttestaccesssecretsodontworry");
        authenticationFilter = new AuthenticationFilter(jwtProvider);
        filterChain = new MockFilterChain();
    }

    @Nested
    @DisplayName("preHandle 호출 시")
    class PreHandleTest {

        MockHttpServletRequest httpServletRequest;
        MockHttpServletResponse httpServletResponse;

        @BeforeEach
        void setUp() {
            httpServletRequest = new MockHttpServletRequest();
            httpServletResponse = new MockHttpServletResponse();
        }

        @Nested
        @DisplayName("authorization 헤더가 포함되어 있으면")
        class ContainsAuthorizationHeader {

            private Member member;
            private String accessToken;

            @BeforeEach
            void setUp() {
                member = new Member("email@email.com", "password", MemberRole.USER);
                accessToken = jwtProvider.createAccessToken(member);
            }

            @Test
            @DisplayName("인증 프로세스를 진행한다.")
            void runAuthenticationProcess_WhenContainsAuthorizationHeader() throws Exception {
                // given
                String bearerAccessToken = "Bearer " + accessToken;
                httpServletRequest.addHeader("Authorization", bearerAccessToken);

                // when
                authenticationFilter.doFilterInternal(
                        httpServletRequest, httpServletResponse, filterChain);

                // then
                CustomClaims customClaims = jwtProvider.parseAccessToken(accessToken);
                Authentication authentication =
                        SecurityContextHolder.getContext().getAuthentication();
                assertThat(authentication)
                        .isNotNull()
                        .isInstanceOf(UsernamePasswordAuthenticationToken.class)
                        .satisfies(
                                auth -> {
                                    assertThat(auth.getPrincipal())
                                            .isEqualTo(customClaims.getEmail());
                                    assertThat(auth.getAuthorities())
                                            .map(GrantedAuthority::getAuthority)
                                            .containsExactlyElementsOf(
                                                    customClaims.getMemberRole().getAuthorities());
                                });
            }

            @Test
            @DisplayName("예외(authentication): 액세스 토큰 형식이 Bearer가 아니면")
            void authentication_WhenAccessTokenTypeIsNotBearer() {
                // given
                String invalidAccessToken = "invalid" + accessToken;
                httpServletRequest.addHeader("Authorization", invalidAccessToken);

                // when
                Exception exception =
                        catchException(
                                () ->
                                        authenticationFilter.doFilterInternal(
                                                httpServletRequest,
                                                httpServletResponse,
                                                filterChain));

                // then
                assertThat(exception)
                        .isInstanceOf(TicketingException.class)
                        .extracting("errorCode")
                        .isEqualTo(ErrorCode.INVALID_TOKEN_HEADER);
            }
        }

        @Nested
        @DisplayName("Authorization 헤더가 포함되어 있지 않으면")
        class NotContainsAuthorizationHeader {

            @Test
            @DisplayName("무시한다.")
            void ignoreAuthenticationProcess_WhenNotContainsAuthorizationHeader() throws Exception {
                // given

                // when
                authenticationFilter.doFilterInternal(
                        httpServletRequest, httpServletResponse, filterChain);

                // then
                Authentication authentication =
                        SecurityContextHolder.getContext().getAuthentication();
                assertThat(authentication).isNull();
            }
        }
    }
}
