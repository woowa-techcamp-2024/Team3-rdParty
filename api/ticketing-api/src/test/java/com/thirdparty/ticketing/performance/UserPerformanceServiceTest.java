package com.thirdparty.ticketing.performance;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.thirdparty.common.ItemResult;
import com.thirdparty.ticketing.jpa.performance.Performance;
import com.thirdparty.ticketing.jpa.performance.PerformanceRepository;
import com.thirdparty.ticketing.performance.dto.PerformanceElement;
import com.thirdparty.ticketing.performance.service.UserPerformanceService;

@DataJpaTest
class UserPerformanceServiceTest {

    private UserPerformanceService userPerformanceService;

    @Autowired private TestEntityManager testEntityManager;

    @Autowired private PerformanceRepository performanceRepository;

    @BeforeEach
    void setUpBase() {
        userPerformanceService = new UserPerformanceService(performanceRepository);
    }

    @Nested
    @DisplayName("getPerformances 메서드를 호출할 때")
    class GetPerformances {

        @Test
        @DisplayName("공연을 성공적으로 조회한다.")
        void getPerformances_Success() {
            // Given
            ZonedDateTime showtime1 =
                    ZonedDateTime.of(2024, 8, 23, 14, 30, 0, 0, ZoneId.of("Asia/Seoul"));
            ZonedDateTime showtime2 =
                    ZonedDateTime.of(2024, 8, 24, 15, 0, 0, 0, ZoneId.of("Asia/Seoul"));

            Performance performance1 =
                    Performance.builder()
                            .performanceName("공연 1")
                            .performancePlace("장소 1")
                            .performanceShowtime(showtime1)
                            .build();

            Performance performance2 =
                    Performance.builder()
                            .performanceName("공연 2")
                            .performancePlace("장소 2")
                            .performanceShowtime(showtime2)
                            .build();

            testEntityManager.persist(performance1);
            testEntityManager.persist(performance2);
            testEntityManager.flush();

            // When
            ItemResult<PerformanceElement> performanceElements =
                    userPerformanceService.getPerformances();

            // Then
            assertThat(performanceElements.getItems())
                    .isNotEmpty()
                    .hasSize(2)
                    .satisfies(
                            elements -> {
                                assertThat(elements.get(0))
                                        .satisfies(
                                                element -> {
                                                    assertThat(element.getPerformanceName())
                                                            .isEqualTo("공연 1");
                                                    assertThat(element.getPerformancePlace())
                                                            .isEqualTo("장소 1");
                                                    assertThat(element.getPerformanceShowtime())
                                                            .isEqualTo(showtime1);
                                                });
                                assertThat(elements.get(1))
                                        .satisfies(
                                                element -> {
                                                    assertThat(element.getPerformanceName())
                                                            .isEqualTo("공연 2");
                                                    assertThat(element.getPerformancePlace())
                                                            .isEqualTo("장소 2");
                                                    assertThat(element.getPerformanceShowtime())
                                                            .isEqualTo(showtime2);
                                                });
                            });
        }
    }
}
