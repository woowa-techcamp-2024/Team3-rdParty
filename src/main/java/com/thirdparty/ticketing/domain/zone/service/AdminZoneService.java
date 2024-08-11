package com.thirdparty.ticketing.domain.zone.service;

import com.thirdparty.ticketing.domain.performance.Performance;
import com.thirdparty.ticketing.domain.performance.repository.PerformanceRepository;
import com.thirdparty.ticketing.domain.zone.Zone;
import com.thirdparty.ticketing.domain.zone.dto.ZoneCreationRequest;
import com.thirdparty.ticketing.domain.zone.repository.ZoneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    private List<Zone> convertDtoToEntity(Performance performance, ZoneCreationRequest zoneCreationRequest) {
        return zoneCreationRequest.getZones()
                .stream()
                .map(zoneElement ->
                        Zone.builder()
                                .performance(performance)
                                .zoneName(zoneElement.getZoneName())
                                .build()
                ).toList();
    }
}
