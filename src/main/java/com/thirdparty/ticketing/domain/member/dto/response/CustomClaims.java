package com.thirdparty.ticketing.domain.member.dto.response;

import com.thirdparty.ticketing.domain.member.MemberRole;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class CustomClaims {
    private final String email;
    private final MemberRole memberRole;
}
