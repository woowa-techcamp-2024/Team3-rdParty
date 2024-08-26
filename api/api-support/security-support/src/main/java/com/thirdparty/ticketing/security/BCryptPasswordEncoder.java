package com.thirdparty.ticketing.security;

import java.util.NoSuchElementException;

import com.thirdparty.ticketing.jpa.member.Member;
import com.thirdparty.ticketing.security.service.PasswordEncoder;

public class BCryptPasswordEncoder implements PasswordEncoder {

    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder =
            new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();

    @Override
    public String encode(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    @Override
    public void checkMatches(Member member, String rawPassword) {
        if (passwordEncoder.matches(rawPassword, member.getPassword())) {
            return;
        }
        throw new NoSuchElementException("아이디/패스워드가 일치하지 않습니다.");
    }
}
