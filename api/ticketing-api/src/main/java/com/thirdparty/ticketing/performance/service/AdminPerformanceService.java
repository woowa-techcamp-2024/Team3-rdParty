package com.thirdparty.ticketing.performance.service;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.thirdparty.ticketing.jpa.performance.Performance;
import com.thirdparty.ticketing.jpa.performance.PerformanceRepository;
import com.thirdparty.ticketing.performance.dto.request.PerformanceCreationRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminPerformanceService {

    private final PerformanceRepository performanceRepository;

    @Transactional
    public void createPerformance(PerformanceCreationRequest performanceCreationRequest) {
        Performance performance = convertDtoToEntity(performanceCreationRequest);
        performanceRepository.save(performance);
    }

    private Performance convertDtoToEntity(PerformanceCreationRequest performanceCreationRequest) {
        return Performance.builder()
                .performanceName(performanceCreationRequest.getPerformanceName())
                .performancePlace(performanceCreationRequest.getPerformancePlace())
                .performanceShowtime(performanceCreationRequest.getPerformanceShowtime())
                .build();
    }
}
