package com.thirdparty.ticketing.domain.member.controller;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thirdparty.ticketing.domain.member.dto.request.MemberCreationRequest;
import com.thirdparty.ticketing.domain.member.service.MemberService;
import com.thirdparty.ticketing.domain.member.dto.response.CreateMemberResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;

    @PostMapping
    public ResponseEntity<CreateMemberResponse> createMember(
            @RequestBody @Valid MemberCreationRequest request) {
        CreateMemberResponse response = memberService.createMember(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
