package com.thirdparty.ticketing.global.config;

import com.thirdparty.ticketing.domain.member.Member;
import com.thirdparty.ticketing.domain.member.service.JwtProvider;
import com.thirdparty.ticketing.domain.member.service.PasswordEncoder;
import com.thirdparty.ticketing.global.security.JJwtProvider;
import java.util.NoSuchElementException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new PasswordEncoder() {
            @Override
            public String encode(String rawPassword) {
                return new StringBuilder(rawPassword).reverse().toString();
            }

            @Override
            public void checkMatches(Member member, String rawPassword) {
                if (encode(rawPassword).equals(member.getPassword())) {
                    return;
                }
                throw new NoSuchElementException("아이디/패스워드가 일치하지 않습니다.");
            }
        };
    }

    @Bean
    public JwtProvider jwtProvider(
            @Value("${jwt.issuer}") String issuer,
            @Value("${jwt.expiry-seconds}") int expirySeconds,
            @Value("${jwt.secret}") String secret) {
        return new JJwtProvider(issuer, expirySeconds, secret);
    }
}
