package com.thirdparty.ticketing.global.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;

import com.thirdparty.ticketing.domain.member.Member;
import com.thirdparty.ticketing.domain.member.MemberRole;
import com.thirdparty.ticketing.domain.member.repository.MemberRepository;
import com.thirdparty.ticketing.domain.member.service.ExpiredTokenException;
import com.thirdparty.ticketing.domain.member.service.InvalidTokenException;
import com.thirdparty.ticketing.domain.member.service.JwtProvider;
import com.thirdparty.ticketing.domain.member.service.response.CustomClaims;
import java.time.ZonedDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class JJwtProviderTest {
    private String issuer;
    private int expirySeconds;
    private String secret;
    private JwtProvider jwtProvider;

    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    void setUp() {
        issuer = "test";
        expirySeconds = 3600;
        secret = "thisisjusttestaccesssecretsodontworry";
        jwtProvider = new JJwtProvider(issuer, expirySeconds, secret);
    }

    @Nested
    @DisplayName("액세스 토큰 생성 메서드 실행 시")
    class CreateAccessTokenTest {

        @Test
        @DisplayName("새로운 액세스 토큰을 반환한다.")
        void createAccessToken() {
            //given
            Member member = new Member("test@test.com", "password", MemberRole.USER, ZonedDateTime.now());
            memberRepository.save(member);

            //when
            String accessToken = jwtProvider.createAccessToken(member);

            //then
            assertThat(accessToken).isNotBlank();
        }
    }

    @Nested
    @DisplayName("액세스 토큰 분석 메서드 실행 시")
    class ParseAccessTokenTest {

        private Member member;
        private String accessToken;

        @BeforeEach
        void setUp() {
            member = new Member("test@test.com", "password", MemberRole.USER, ZonedDateTime.now());
            memberRepository.save(member);

            accessToken = jwtProvider.createAccessToken(member);
        }

        @Test
        @DisplayName("분석한 토큰 페이로드를 반환한다.")
        void parseAccessToken() {
            //given

            //when
            CustomClaims customClaims = jwtProvider.parseAccessToken(accessToken);

            //then
            assertThat(customClaims.getEmail()).isEqualTo(member.getEmail());
            assertThat(customClaims.getMemberRole()).isEqualTo(member.getMemberRole());
        }

        @Test
        @DisplayName("예외(invalidToken): 액세스 토큰이 유효하지 않으면")
        void invalidToken_WhenAccessTokenIsInvalid() {
            //given
            String notEqualSecret = secret + "asdf";
            JJwtProvider invalidJJwtProvider = new JJwtProvider(issuer, expirySeconds, notEqualSecret);
            String invalidAccessToken = invalidJJwtProvider.createAccessToken(member);

            //when
            Exception exception = catchException(() -> jwtProvider.parseAccessToken(invalidAccessToken));

            //then
            assertThat(exception).isInstanceOf(InvalidTokenException.class);
        }

        @Test
        @DisplayName("예외(expiredToken): 액세스 토큰이 만료되었으면")
        void expiredToken_WhenAccessTokenIsExpired() {
            //given
            int alreadyExpiredSeconds = -1;
            JJwtProvider expiredJJwtProvider = new JJwtProvider(issuer, alreadyExpiredSeconds, secret);
            String expiredAccessToken = expiredJJwtProvider.createAccessToken(member);

            //when
            Exception exception = catchException(() -> jwtProvider.parseAccessToken(expiredAccessToken));

            //then
            assertThat(exception).isInstanceOf(ExpiredTokenException.class);
        }
    }
}
