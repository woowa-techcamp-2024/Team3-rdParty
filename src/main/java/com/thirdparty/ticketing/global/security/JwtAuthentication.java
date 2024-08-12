package com.thirdparty.ticketing.global.security;

import com.thirdparty.ticketing.domain.member.MemberRole;
import java.util.Set;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JwtAuthentication {

    private final String email;
    private final MemberRole memberRole;
    private final String accessToken;

    public String getPrincipal() {
        return email;
    }

    public String getAuthority() {
        return memberRole.getValue();
    }

    public String getCredential() {
        return accessToken;
    }

    public Set<String> getAuthorities() {
        return memberRole.getAuthorities();
    }
}
