package com.thirdparty.ticketing.zone.contoller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thirdparty.common.ItemResult;
import com.thirdparty.ticketing.zone.dto.ZoneElement;
import com.thirdparty.ticketing.zone.service.UserZoneService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/performances/{performanceId}/zones")
public class UserZoneController {

    private final UserZoneService userZoneService;

    @GetMapping
    public ResponseEntity<ItemResult<ZoneElement>> getZones(
            @PathVariable("performanceId") long performanceId) {
        return ResponseEntity.ok(userZoneService.getZones(performanceId));
    }
}
