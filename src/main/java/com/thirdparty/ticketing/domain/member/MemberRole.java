package com.thirdparty.ticketing.domain.member;

import java.util.Arrays;
import lombok.Getter;

@Getter
public enum MemberRole {
    USER("ROLE_USER"), ADMIN("ROLE_ADMIN");

    private final String value;

    MemberRole(String value) {
        this.value = value;
    }

    public static MemberRole find(String value) {
        return Arrays.stream(values())
                .filter(role -> role.value.equals(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당하는 역할이 존재하지 않습니다."));
    }
}
