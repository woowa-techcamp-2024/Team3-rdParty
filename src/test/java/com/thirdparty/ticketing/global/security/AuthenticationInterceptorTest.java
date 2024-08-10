package com.thirdparty.ticketing.global.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;

import com.thirdparty.ticketing.domain.member.Member;
import com.thirdparty.ticketing.domain.member.MemberRole;
import com.thirdparty.ticketing.domain.member.service.JwtProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

class AuthenticationInterceptorTest {

    private AuthenticationInterceptor authenticationInterceptor;
    private JwtProvider jwtProvider;
    private AuthenticationContext authenticationContext;

    @BeforeEach
    void setUp() {
        authenticationContext = new AuthenticationContext();
        jwtProvider = new JJwtProvider("test", 3600, "thisisjusttestaccesssecretsodontworry");
        authenticationInterceptor = new AuthenticationInterceptor(jwtProvider,
            authenticationContext);
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
                //given
                String bearerAccessToken = "Bearer " + accessToken;
                httpServletRequest.addHeader("Authorization", bearerAccessToken);

                //when
                boolean result = authenticationInterceptor.preHandle(httpServletRequest,
                    httpServletResponse,
                    null);

                //then
                assertThat(result).isTrue();
                Authentication authentication = authenticationContext.getAuthentication();
                assertThat(authentication).isNotNull();
            }

            @Test
            @DisplayName("예외(authentication): 액세스 토큰 형식이 Beaerer가 아니면")
            void authentication_WhenAccessTokenTypeIsNotBearer() {
                //given
                String invalidAccessToken = "invalid" + accessToken;
                httpServletRequest.addHeader("Authorization", invalidAccessToken);

                //when
                Exception exception = catchException(
                    () -> authenticationInterceptor.preHandle(httpServletRequest,
                        httpServletResponse, null));

                //then
                assertThat(exception).isInstanceOf(AuthenticationException.class);
            }
        }

        @Nested
        @DisplayName("Authorization 헤더가 포함되어 있지 않으면")
        class NotContainsAuthorizationHeader {

            @Test
            @DisplayName("무시한다.")
            void ignoreAuthenticationProcess_WhenNotContainsAuthorizationHeader() throws Exception {
                //given
                MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();

                //when
                boolean result = authenticationInterceptor.preHandle(mockHttpServletRequest,
                    httpServletResponse,
                    null);

                //then
                assertThat(result).isTrue();
                Authentication authentication = authenticationContext.getAuthentication();
                assertThat(authentication).isNull();
            }
        }
    }

    @Nested
    @DisplayName("afterCompletion 호출 시")
    class AfterCompletionTest {

        private MockHttpServletRequest httpServletRequest;
        private MockHttpServletResponse httpServletResponse;
        private Member member;
        private String accessToken;

        @BeforeEach
        void setUp() {
            httpServletRequest = new MockHttpServletRequest();
            httpServletResponse = new MockHttpServletResponse();
            member = new Member("email@email.com", "password", MemberRole.USER);
            accessToken = jwtProvider.createAccessToken(member);
        }

        @Test
        @DisplayName("인증 컨텍스트에서 사용자 정보를 제거한다.")
        void afterCompletion() throws Exception {
            //given
            String bearerAccessToken = "Bearer " + accessToken;
            httpServletRequest.addHeader("Authorization", bearerAccessToken);
            authenticationInterceptor.preHandle(httpServletRequest, httpServletResponse,
                null);

            //when
            authenticationInterceptor.afterCompletion(httpServletRequest,
                httpServletResponse, null, null);

            //then
            Authentication authentication = authenticationContext.getAuthentication();
            assertThat(authentication).isNull();
        }
    }
}