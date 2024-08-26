package com.thirdparty.ticketing.member.dto.response;

import com.thirdparty.ticketing.jpa.member.Member;

import lombok.Data;

@Data
public class LoginResponse {
    private Long memberId;
    private String accessToken;

    public LoginResponse(Long memberId, String accessToken) {
        this.memberId = memberId;
        this.accessToken = accessToken;
    }

    public static LoginResponse of(Member member, String accessToken) {
        return new LoginResponse(member.getMemberId(), accessToken);
    }
}
