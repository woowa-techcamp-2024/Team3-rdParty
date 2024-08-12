package com.thirdparty.ticketing.domain.zone.service;

import com.thirdparty.ticketing.domain.performance.Performance;
import com.thirdparty.ticketing.domain.performance.repository.PerformanceRepository;
import com.thirdparty.ticketing.domain.zone.dto.ZoneElement;
import com.thirdparty.ticketing.domain.zone.repository.ZoneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberZoneService {

    private final ZoneRepository zoneRepository;
    private final PerformanceRepository performanceRepository;

    public List<ZoneElement> getZones(Long performanceId) {
        Performance performance = performanceRepository.findById(performanceId).orElseThrow();
        return zoneRepository.findByPerformance(performance)
                .stream()
                .map(ZoneElement::of)
                .toList();
    }
}