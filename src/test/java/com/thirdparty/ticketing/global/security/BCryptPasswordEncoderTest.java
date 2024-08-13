package com.thirdparty.ticketing.global.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;

import java.util.NoSuchElementException;
import java.util.regex.Pattern;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.thirdparty.ticketing.domain.member.Member;

class BCryptPasswordEncoderTest {

    private BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
    private static final Pattern BCRYPT_PATTERN =
            Pattern.compile("\\A\\$2(a|y|b)?\\$(\\d\\d)\\$[./0-9A-Za-z]{53}");

    @Nested
    @DisplayName("encode 호출 시")
    class EncodeTest {

        @Test
        @DisplayName("패스워드를 암호화한다.")
        void encodePassword() {
            // given
            String rawPassword = "password";

            // when
            String encodedPassword = bCryptPasswordEncoder.encode(rawPassword);

            // then
            assertThat(encodedPassword).isNotEqualTo(rawPassword);
        }

        @Test
        @DisplayName("패스워드를 BCrypt 형식으로 암호화한다.")
        void encodePassword_UsingBCrypt() {
            // given
            String rawPassword = "password";

            // when
            String encodedPassword = bCryptPasswordEncoder.encode(rawPassword);

            // then
            assertThat(BCRYPT_PATTERN.matcher(encodedPassword).matches()).isTrue();
        }
    }

    @Nested
    @DisplayName("checkPassword 호출 시")
    class CheckPasswordTest {

        @Test
        @DisplayName("사용자의 암호화된 패스워드와 입력된 raw 패스워드가 같으면 예외를 던지지 않는다.")
        void doesNotThrowException_WhenEqualsMemberPasswordAndRawPassword() {
            // given
            String rawPassword = "password";
            Member member =
                    Member.builder().password(bCryptPasswordEncoder.encode(rawPassword)).build();

            // when
            Exception exception =
                    catchException(() -> bCryptPasswordEncoder.checkMatches(member, rawPassword));

            // then
            assertThat(exception).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("예외(noSuchElement): 사용자의 암호화된 패스워드와 입력된 raw 패스워드가 같지 않으면")
        void noSuchElement_WhenNotEqualsMemberPasswordAndRawPassword() {
            // given
            String rawPassword = "password";
            Member member =
                    Member.builder().password(bCryptPasswordEncoder.encode(rawPassword)).build();
            String wrongPassword = rawPassword + "a";

            // when
            Exception exception =
                    catchException(() -> bCryptPasswordEncoder.checkMatches(member, wrongPassword));

            // then
            assertThat(exception).isInstanceOf(NoSuchElementException.class);
        }
    }
}
