package com.thirdparty.ticketing.domain.performance.service;

import com.thirdparty.ticketing.domain.ItemResult;
import com.thirdparty.ticketing.domain.performance.dto.PerformanceElement;
import com.thirdparty.ticketing.domain.performance.repository.PerformanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserPerformanceService {

    private final PerformanceRepository performanceRepository;

    public ItemResult<PerformanceElement> getPerformances() {
        return ItemResult.of(performanceRepository.findAll()
                .stream()
                .map(PerformanceElement::of)
                .toList());
    }
}
