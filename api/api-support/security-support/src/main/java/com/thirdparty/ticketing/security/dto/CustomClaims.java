package com.thirdparty.ticketing.security.dto;

import com.thirdparty.ticketing.jpa.member.MemberRole;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class CustomClaims {
    private final String email;
    private final MemberRole memberRole;
}
