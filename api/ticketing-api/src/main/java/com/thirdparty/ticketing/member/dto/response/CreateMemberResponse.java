package com.thirdparty.ticketing.member.dto.response;

import com.thirdparty.ticketing.jpa.member.Member;

import lombok.Data;

@Data
public class CreateMemberResponse {
    private final Long memberId;

    public static CreateMemberResponse from(Member member) {
        return new CreateMemberResponse(member.getMemberId());
    }
}
