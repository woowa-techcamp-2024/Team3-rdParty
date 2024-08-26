package com.thirdparty.ticketing.zone.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thirdparty.common.ItemResult;
import com.thirdparty.ticketing.exception.ErrorCode;
import com.thirdparty.ticketing.exception.TicketingException;
import com.thirdparty.ticketing.jpa.performance.Performance;
import com.thirdparty.ticketing.jpa.performance.PerformanceRepository;
import com.thirdparty.ticketing.jpa.zone.repository.ZoneRepository;
import com.thirdparty.ticketing.zone.dto.ZoneElement;

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
                        .orElseThrow(() -> new TicketingException(ErrorCode.NOT_FOUND_PERFORMANCE));
        return ItemResult.of(
                zoneRepository.findByPerformance(performance).stream()
                        .map(ZoneElement::of)
                        .toList());
    }
}
