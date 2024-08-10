package com.thirdparty.ticketing.domain.member.service.response;

import com.thirdparty.ticketing.domain.member.MemberRole;
import lombok.Data;

@Data
public class CustomClaims {
    private Long memberId;
    private MemberRole memberRole;

    public CustomClaims(Long memberId, MemberRole memberRole) {
        this.memberId = memberId;
        this.memberRole = memberRole;
    }
}
