package com.thirdparty.ticketing.domain.seat.controller;

import com.thirdparty.ticketing.domain.common.TicketingException;
import com.thirdparty.ticketing.domain.member.MemberRole;
import com.thirdparty.ticketing.domain.seat.dto.SeatCreationRequest;
import com.thirdparty.ticketing.domain.seat.service.AdminSeatService;
import com.thirdparty.ticketing.global.security.AuthenticationContext;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/performances/{performancesId}/zones/{zoneId}/seats")
@RequiredArgsConstructor
public class AdminSeatController {
    private final AdminSeatService adminSeatService;
    private final AuthenticationContext authenticationContext;

    @PostMapping
    public ResponseEntity<Void> createSeats(
            @PathVariable("zoneId") Long zoneId,
            @RequestBody @Valid SeatCreationRequest seatCreationRequest
    ) {
        String authority = authenticationContext.getAuthentication().getAuthority();
        MemberRole memberRole = MemberRole.find(authority);
        if (memberRole != MemberRole.ADMIN) {
            throw new TicketingException("");
        }

        adminSeatService.createSeats(zoneId, seatCreationRequest);

        return ResponseEntity.status(HttpStatus.CREATED)
                .build();
    }
}
