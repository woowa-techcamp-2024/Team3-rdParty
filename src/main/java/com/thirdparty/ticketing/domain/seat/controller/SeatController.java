package com.thirdparty.ticketing.domain.seat.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thirdparty.ticketing.domain.ItemResult;
import com.thirdparty.ticketing.domain.seat.dto.SeatElement;
import com.thirdparty.ticketing.domain.seat.service.SeatService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SeatController {
    private final SeatService seatService;

    @GetMapping("/performances/{performanceId}/zones/{zoneId}/seats")
    public ResponseEntity<ItemResult<SeatElement>> getSeats(@PathVariable("zoneId") long zoneId) {
        ItemResult<SeatElement> seats = seatService.getSeats(zoneId);
        return ResponseEntity.ok().body(seats);
    }
}
