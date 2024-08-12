package com.thirdparty.ticketing.domain.member.service.response;

import com.thirdparty.ticketing.domain.member.Member;
import lombok.Data;

@Data
public class CreateMemberResponse {
    private final Long memberId;

    public static CreateMemberResponse from(Member member) {
        return new CreateMemberResponse(member.getMemberId());
    }
}
