package com.thirdparty.ticketing.domain.seat.controller;

import com.thirdparty.ticketing.domain.waitingsystem.Waiting;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thirdparty.ticketing.domain.ItemResult;
import com.thirdparty.ticketing.domain.seat.dto.response.SeatElement;
import com.thirdparty.ticketing.domain.seat.dto.response.SeatGradeElement;
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

    @Waiting
    @GetMapping("/performances/{performanceId}/seats")
    public ResponseEntity<ItemResult<SeatElement>> getAllSeats(
            @PathVariable("performanceId") long performanceId) {
        ItemResult<SeatElement> seats = seatService.getAllSeats(performanceId);
        return ResponseEntity.ok().body(seats);
    }

    @GetMapping("/performances/{performanceId}/grades")
    public ResponseEntity<ItemResult<SeatGradeElement>> getSeatGrades(
            @PathVariable("performanceId") long performanceId) {
        ItemResult<SeatGradeElement> seatGrades = seatService.getSeatGrades(performanceId);
        return ResponseEntity.ok().body(seatGrades);
    }
}
