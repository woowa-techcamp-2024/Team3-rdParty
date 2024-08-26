package com.thirdparty.ticketing.performance.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thirdparty.common.ItemResult;
import com.thirdparty.ticketing.exception.ErrorCode;
import com.thirdparty.ticketing.exception.TicketingException;
import com.thirdparty.ticketing.jpa.performance.PerformanceRepository;
import com.thirdparty.ticketing.performance.dto.PerformanceElement;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserPerformanceService {

    private final PerformanceRepository performanceRepository;

    @Transactional(readOnly = true)
    public ItemResult<PerformanceElement> getPerformances() {
        return ItemResult.of(
                performanceRepository.findAll().stream().map(PerformanceElement::of).toList());
    }

    @Transactional(readOnly = true)
    public PerformanceElement getPerformance(long performanceId) {

        return performanceRepository
                .findById(performanceId)
                .map(PerformanceElement::of)
                .orElseThrow(() -> new TicketingException(ErrorCode.NOT_FOUND_PERFORMANCE));
    }
}
