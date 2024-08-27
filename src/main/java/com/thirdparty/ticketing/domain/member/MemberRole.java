package com.thirdparty.ticketing.domain.member;

import java.util.Arrays;
import java.util.Set;

import lombok.Getter;

@Getter
public enum MemberRole {
    USER(Constant.ROLE_USER, Set.of(Constant.ROLE_USER)),
    ADMIN(Constant.ROLE_ADMIN, Set.of(Constant.ROLE_USER, Constant.ROLE_ADMIN));

    private final String value;
    private final Set<String> authorities;

    MemberRole(String value, Set<String> authorities) {
        this.value = value;
        this.authorities = authorities;
    }

    public static MemberRole find(String value) {
        return Arrays.stream(values())
                .filter(role -> role.value.equals(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당하는 역할이 존재하지 않습니다."));
    }

    private static class Constant {
        private static final String ROLE_USER = "ROLE_USER";
        private static final String ROLE_ADMIN = "ROLE_ADMIN";
    }
}
