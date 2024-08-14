package com.thirdparty.ticketing.domain.member.service;

import com.thirdparty.ticketing.domain.member.Member;
import com.thirdparty.ticketing.domain.member.dto.response.CustomClaims;

public interface JwtProvider {
    CustomClaims parseAccessToken(String accessToken);

    String createAccessToken(Member member);
}
