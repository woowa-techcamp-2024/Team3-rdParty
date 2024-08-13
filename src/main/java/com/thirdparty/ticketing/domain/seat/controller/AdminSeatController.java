package com.thirdparty.ticketing.domain.seat.controller;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thirdparty.ticketing.domain.seat.dto.SeatCreationRequest;
import com.thirdparty.ticketing.domain.seat.service.AdminSeatService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/performances/{performancesId}/zones/{zoneId}/seats")
@RequiredArgsConstructor
public class AdminSeatController {
    private final AdminSeatService adminSeatService;

    @PostMapping
    public ResponseEntity<Void> createSeats(
            @PathVariable("zoneId") Long zoneId,
            @RequestBody @Valid SeatCreationRequest seatCreationRequest) {
        adminSeatService.createSeats(zoneId, seatCreationRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
