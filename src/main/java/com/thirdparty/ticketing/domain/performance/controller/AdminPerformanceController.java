package com.thirdparty.ticketing.domain.performance.controller;

import com.thirdparty.ticketing.domain.common.TicketingException;
import com.thirdparty.ticketing.domain.member.MemberRole;
import com.thirdparty.ticketing.domain.performance.controller.request.PerformanceCreationRequest;
import com.thirdparty.ticketing.domain.performance.service.AdminPerformanceService;
import com.thirdparty.ticketing.global.security.Authentication;
import com.thirdparty.ticketing.global.security.AuthenticationContext;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/performances")
public class AdminPerformanceController {

    private final AuthenticationContext authenticationContext;
    private final AdminPerformanceService adminPerformanceService;

    @PostMapping
    public ResponseEntity<Void> createPerformance(
            @RequestBody @Valid PerformanceCreationRequest performanceCreationRequest
    ) {
        Authentication authentication = authenticationContext.getAuthentication();
        String authority = authentication.getAuthority();
        MemberRole memberRole = MemberRole.find(authority);

        if (memberRole != MemberRole.ADMIN) {
            throw new TicketingException("");
        }

        adminPerformanceService.createPerformance(performanceCreationRequest);

        return ResponseEntity.status(HttpStatus.CREATED)
                .build();
    }
}
