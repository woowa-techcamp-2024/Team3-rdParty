package com.thirdparty.ticketing.domain.performance.service;

import com.thirdparty.ticketing.domain.performance.Performance;
import com.thirdparty.ticketing.domain.performance.dto.request.PerformanceCreationRequest;
import com.thirdparty.ticketing.domain.performance.repository.PerformanceRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
