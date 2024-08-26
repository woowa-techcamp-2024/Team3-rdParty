package com.thirdparty.ticketing.security.jwt;

import com.thirdparty.ticketing.jpa.member.Member;
import com.thirdparty.ticketing.security.dto.CustomClaims;

public interface JwtProvider {
    CustomClaims parseAccessToken(String accessToken);

    String createAccessToken(Member member);
}
