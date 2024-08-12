package com.thirdparty.ticketing.domain.performance.service;

import com.thirdparty.ticketing.domain.performance.Performance;
import com.thirdparty.ticketing.domain.performance.controller.request.PerformanceCreationRequest;
import com.thirdparty.ticketing.domain.performance.repository.PerformanceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class AdminPerformanceServiceTest {

    private AdminPerformanceService adminPerformanceService;

    @Autowired
    private PerformanceRepository performanceRepository;

    @BeforeEach
    void setUpBase() {
        adminPerformanceService = new AdminPerformanceService(performanceRepository);
    }

    @Nested
    @DisplayName("createPerformance 메서드를 호출할 때")
    class CreatePerformance {

        private PerformanceCreationRequest performanceCreationRequest;

        @BeforeEach
        void setUp() {
            performanceCreationRequest = new PerformanceCreationRequest();
            performanceCreationRequest.setPerformanceName("공연 이름");
            performanceCreationRequest.setPerformancePlace("공연 장소");
            performanceCreationRequest.setPerformanceShowtime(ZonedDateTime.now());
        }

        @Test
        @DisplayName("공연이 성공적으로 생성된다.")
        void createPerformance_Success() {
            // When
            adminPerformanceService.createPerformance(performanceCreationRequest);

            // Then
            Performance performance = performanceRepository.findById(1L).orElseThrow();
            assertEquals(performance.getPerformanceName(), performanceCreationRequest.getPerformanceName());
            assertEquals(performance.getPerformancePlace(), performanceCreationRequest.getPerformancePlace());
        }
    }

}