package com.thirdparty.ticketing.domain.performance.service;

import static org.assertj.core.api.Assertions.*;

import java.time.ZonedDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.thirdparty.ticketing.domain.performance.Performance;
import com.thirdparty.ticketing.domain.performance.controller.request.PerformanceCreationRequest;
import com.thirdparty.ticketing.domain.performance.repository.PerformanceRepository;

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

		private PerformanceCreationRequest request;

		@BeforeEach
		void setUp() {
			request = new PerformanceCreationRequest();
			request.setPerformanceName("공연 이름");
			request.setPerformancePlace("공연 장소");
			request.setPerformanceShowtime(ZonedDateTime.now());
		}

		@Test
		@DisplayName("공연이 성공적으로 생성된다.")
		void createPerformance_Success() {
			// When
			adminPerformanceService.createPerformance(request);

			// Then
			Optional<Performance> optionalPerformance = performanceRepository.findAll().stream().findFirst();

			assertThat(optionalPerformance).isNotEmpty().get().satisfies(
				performance -> {
					assertThat(performance.getPerformanceName()).isEqualTo(request.getPerformanceName());
					assertThat(performance.getPerformancePlace()).isEqualTo(request.getPerformancePlace());
				}
			);
		}
	}

}
