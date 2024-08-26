package com.thirdparty.ticketing.member.service;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.thirdparty.ticketing.exception.ErrorCode;
import com.thirdparty.ticketing.exception.TicketingException;
import com.thirdparty.ticketing.jpa.member.Member;
import com.thirdparty.ticketing.jpa.member.MemberRole;
import com.thirdparty.ticketing.jpa.member.repository.MemberRepository;
import com.thirdparty.ticketing.member.dto.request.MemberCreationRequest;
import com.thirdparty.ticketing.member.dto.response.CreateMemberResponse;
import com.thirdparty.ticketing.security.service.PasswordEncoder;

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
