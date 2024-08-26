package com.thirdparty.ticketing.security.service;

import com.thirdparty.ticketing.jpa.member.Member;

public interface PasswordEncoder {
    String encode(String rawPassword);

    void checkMatches(Member member, String rawPassword);
}
