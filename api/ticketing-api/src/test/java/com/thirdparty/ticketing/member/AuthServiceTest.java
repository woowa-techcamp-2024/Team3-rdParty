package com.thirdparty.ticketing.member;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;

import java.time.ZonedDateTime;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.thirdparty.ticketing.jpa.member.Member;
import com.thirdparty.ticketing.jpa.member.MemberRole;
import com.thirdparty.ticketing.jpa.member.repository.MemberRepository;
import com.thirdparty.ticketing.member.dto.response.LoginResponse;
import com.thirdparty.ticketing.member.service.AuthService;
import com.thirdparty.ticketing.security.jwt.JJwtProvider;
import com.thirdparty.ticketing.security.jwt.JwtProvider;
import com.thirdparty.ticketing.security.service.PasswordEncoder;

@DataJpaTest
class AuthServiceTest {

    @Autowired private MemberRepository memberRepository;

    private PasswordEncoder passwordEncoder;
    private JwtProvider jwtProvider =
            new JJwtProvider("test", 3600, "thisisjusttestaccesssecretsodontworry");

    @BeforeEach
    void setUp() {
        passwordEncoder =
                new PasswordEncoder() {

                    @Override
                    public String encode(String rawPassword) {
                        return new StringBuilder(rawPassword).reverse().toString();
                    }

                    @Override
                    public void checkMatches(Member member, String rawPassword) {
                        String encodedPassword = encode(rawPassword);
                        if (encodedPassword.equals(member.getPassword())) {
                            return;
                        }
                        throw new NoSuchElementException("이메일/패스워드가 일치하지 않습니다.");
                    }
                };
    }

    @Nested
    @DisplayName("로그인 메서드 호출 시")
    class LoginTest {

        private String email;
        private String rawPassword;
        private Member savedMember;
        private AuthService authService;

        @BeforeEach
        void setUp() {
            email = "test@test.com";
            rawPassword = "test1234";
            String password = passwordEncoder.encode(rawPassword);
            MemberRole memberRole = MemberRole.USER;
            savedMember = new Member(email, password, memberRole, ZonedDateTime.now());
            authService = new AuthService(memberRepository, passwordEncoder, jwtProvider);
        }

        @Test
        @DisplayName("액세스 토큰과 회원 ID를 반환한다.")
        void login() {
            // given
            memberRepository.save(savedMember);

            // when
            LoginResponse response = authService.login(email, rawPassword);

            // then
            assertThat(response.getAccessToken()).isNotBlank();
            assertThat(response.getMemberId()).isEqualTo(savedMember.getMemberId());
        }

        @Test
        @DisplayName("예외(noSuchElement): 이메일과 일치하는 회원이 없으면")
        void noSuchElement_WhenEmailIsNotMatches() {
            // given
            String wrongEmail = "wrongEmail";
            memberRepository.save(savedMember);

            // when
            Exception exception = catchException(() -> authService.login(wrongEmail, rawPassword));

            // then
            assertThat(exception).isInstanceOf(NoSuchElementException.class);
        }

        @Test
        @DisplayName("예외(noSuchElement): 회원의 비밀번호와 일치하지 않으면")
        void noSuchElement_WhenPasswordIsNotMatches() {
            // given
            String wrongPassword = "wrongPassword";
            memberRepository.save(savedMember);

            // when
            Exception exception = catchException(() -> authService.login(email, wrongPassword));

            // then
            assertThat(exception).isInstanceOf(NoSuchElementException.class);
        }
    }
}
