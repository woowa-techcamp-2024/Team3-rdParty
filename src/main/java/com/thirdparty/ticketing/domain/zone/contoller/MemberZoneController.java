package com.thirdparty.ticketing.domain.zone.contoller;

import com.thirdparty.ticketing.domain.ItemResult;
import com.thirdparty.ticketing.domain.zone.dto.ZoneElement;
import com.thirdparty.ticketing.domain.zone.service.MemberZoneService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/performances/{performanceId}/zones")
public class MemberZoneController {

    private final MemberZoneService memberZoneService;

    @GetMapping
    public ItemResult<ZoneElement> getZones(
            @PathVariable("performanceId") long performanceId) {
        return ItemResult.of(memberZoneService.getZones(performanceId));
    }
}
