package com.thirdparty.ticketing.domain.member.service;

import com.thirdparty.ticketing.domain.member.Member;

public interface PasswordEncoder {
    String encode(String rawPassword);

    void checkMatches(Member member, String rawPassword);
}
