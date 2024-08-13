package com.thirdparty.ticketing.domain.zone.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thirdparty.ticketing.domain.ItemResult;
import com.thirdparty.ticketing.domain.common.TicketingException;
import com.thirdparty.ticketing.domain.performance.Performance;
import com.thirdparty.ticketing.domain.performance.repository.PerformanceRepository;
import com.thirdparty.ticketing.domain.zone.dto.ZoneElement;
import com.thirdparty.ticketing.domain.zone.repository.ZoneRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserZoneService {

    private final ZoneRepository zoneRepository;
    private final PerformanceRepository performanceRepository;

    @Transactional(readOnly = true)
    public ItemResult<ZoneElement> getZones(Long performanceId) {
        Performance performance =
                performanceRepository
                        .findById(performanceId)
                        .orElseThrow(() -> new TicketingException(""));
        return ItemResult.of(
                zoneRepository.findByPerformance(performance).stream()
                        .map(ZoneElement::of)
                        .toList());
    }
}
