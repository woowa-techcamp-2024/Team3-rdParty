package com.thirdparty.ticketing.domain.member.service;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.thirdparty.ticketing.domain.common.ErrorCode;
import com.thirdparty.ticketing.domain.common.TicketingException;
import com.thirdparty.ticketing.domain.member.Member;
import com.thirdparty.ticketing.domain.member.MemberRole;
import com.thirdparty.ticketing.domain.member.controller.request.MemberCreationRequest;
import com.thirdparty.ticketing.domain.member.repository.MemberRepository;
import com.thirdparty.ticketing.domain.member.service.response.CreateMemberResponse;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public CreateMemberResponse createMember(MemberCreationRequest request) {
        memberRepository
                .findByEmail(request.getEmail())
                .ifPresent(
                        member -> {
                            throw new TicketingException(ErrorCode.DUPLICATED_EMAIL);
                        });
        Member member =
                Member.builder()
                        .email(request.getEmail())
                        .password(passwordEncoder.encode(request.getPassword()))
                        .memberRole(MemberRole.USER)
                        .build();
        memberRepository.save(member);
        return CreateMemberResponse.from(member);
    }
}
