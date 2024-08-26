package com.thirdparty.ticketing.zone.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thirdparty.ticketing.jpa.performance.Performance;
import com.thirdparty.ticketing.jpa.performance.PerformanceRepository;
import com.thirdparty.ticketing.jpa.zone.Zone;
import com.thirdparty.ticketing.jpa.zone.repository.ZoneRepository;
import com.thirdparty.ticketing.zone.dto.ZoneCreationRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminZoneService {
    private final ZoneRepository zoneRepository;
    private final PerformanceRepository performanceRepository;

    @Transactional
    public void createZones(Long performanceId, ZoneCreationRequest zoneCreationRequest) {
        Performance performance = performanceRepository.findById(performanceId).orElseThrow();
        List<Zone> zones = convertDtoToEntity(performance, zoneCreationRequest);
        zoneRepository.saveAll(zones);
    }

    private List<Zone> convertDtoToEntity(
            Performance performance, ZoneCreationRequest zoneCreationRequest) {
        return zoneCreationRequest.getZones().stream()
                .map(
                        zoneElement ->
                                Zone.builder()
                                        .performance(performance)
                                        .zoneName(zoneElement.getZoneName())
                                        .build())
                .toList();
    }
}
