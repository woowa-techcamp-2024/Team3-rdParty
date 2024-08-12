package com.thirdparty.ticketing.domain.performance.service;

import com.thirdparty.ticketing.domain.performance.dto.PerformanceElement;
import com.thirdparty.ticketing.domain.performance.repository.PerformanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberPerformanceService {

    private final PerformanceRepository performanceRepository;

    public List<PerformanceElement> getPerformances() {
        return performanceRepository.findAll()
                .stream()
                .map(PerformanceElement::of)
                .toList();
    }
}
