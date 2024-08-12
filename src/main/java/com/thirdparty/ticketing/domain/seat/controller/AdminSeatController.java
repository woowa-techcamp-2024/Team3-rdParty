package com.thirdparty.ticketing.domain.seat.controller;

import com.thirdparty.ticketing.domain.seat.dto.SeatCreationRequest;
import com.thirdparty.ticketing.domain.seat.dto.SeatGradeCreationElement;
import com.thirdparty.ticketing.domain.seat.dto.SeatGradeCreationRequest;
import com.thirdparty.ticketing.domain.seat.service.AdminSeatService;
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
@RequestMapping("/api")
@RequiredArgsConstructor
public class AdminSeatController {
    private final AdminSeatService adminSeatService;

    @PostMapping("/performances/{performanceId}/zones/{zoneId}/seats")
    public ResponseEntity<Void> createSeats(
            @PathVariable("performanceId") long performanceId,
            @PathVariable("zoneId") long zoneId,
            @RequestBody @Valid SeatCreationRequest seatCreationRequest
    ) {
        adminSeatService.createSeats(performanceId, zoneId, seatCreationRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .build();
    }

    @PostMapping("/performances/{performanceId}/grades")
    public ResponseEntity<Void> createSeatGrades(
            @PathVariable("performanceId") long performanceId,
            @RequestBody @Valid SeatGradeCreationRequest seatGradeCreationRequest
    ) {
        adminSeatService.createSeatGrades(performanceId, seatGradeCreationRequest);

        return ResponseEntity.status(HttpStatus.CREATED)
                .build();
    }
}
