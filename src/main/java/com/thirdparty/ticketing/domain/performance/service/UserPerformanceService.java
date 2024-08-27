package com.thirdparty.ticketing.domain.performance.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thirdparty.ticketing.domain.ItemResult;
import com.thirdparty.ticketing.domain.common.ErrorCode;
import com.thirdparty.ticketing.domain.common.TicketingException;
import com.thirdparty.ticketing.domain.performance.dto.PerformanceElement;
import com.thirdparty.ticketing.domain.performance.repository.PerformanceRepository;

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
