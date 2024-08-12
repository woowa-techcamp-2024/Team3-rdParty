package com.thirdparty.ticketing.domain.zone.contoller;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thirdparty.ticketing.domain.zone.dto.ZoneCreationRequest;
import com.thirdparty.ticketing.domain.zone.service.AdminZoneService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/performances/{performanceId}/zones")
@RequiredArgsConstructor
public class AdminZoneController {

    private final AdminZoneService adminZoneService;

    @PostMapping
    public ResponseEntity<Void> createZones(
            @PathVariable("performanceId") long performanceId,
            @RequestBody @Valid ZoneCreationRequest zoneCreationRequest) {
        adminZoneService.createZones(performanceId, zoneCreationRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
