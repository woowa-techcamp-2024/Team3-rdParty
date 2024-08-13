package com.thirdparty.ticketing.domain.performance.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.thirdparty.ticketing.domain.performance.Performance;
import com.thirdparty.ticketing.domain.performance.dto.request.PerformanceCreationRequest;
import com.thirdparty.ticketing.domain.performance.repository.PerformanceRepository;
import java.time.ZonedDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

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

        private PerformanceCreationRequest reuqest;

        @BeforeEach
        void setUp() {
            reuqest = new PerformanceCreationRequest();
            reuqest.setPerformanceName("공연 이름");
            reuqest.setPerformancePlace("공연 장소");
            reuqest.setPerformanceShowtime(ZonedDateTime.now());
        }

        @Test
        @DisplayName("공연이 성공적으로 생성된다.")
        void createPerformance_Success() {
            // When
            adminPerformanceService.createPerformance(reuqest);

            // Then
            Optional<Performance> optionalPerformance = performanceRepository.findAll().stream().findFirst();
            assertThat(optionalPerformance).isNotEmpty().get()
                    .satisfies(performance -> {
                        assertThat(performance.getPerformanceName()).isEqualTo(reuqest.getPerformanceName());
                        assertThat(performance.getPerformancePlace()).isEqualTo(reuqest.getPerformancePlace());
                    });
        }
    }

}
