package com.thirdparty.ticketing.domain.seat.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.thirdparty.ticketing.domain.performance.Performance;
import com.thirdparty.ticketing.domain.seat.Seat;
import com.thirdparty.ticketing.domain.seat.SeatGrade;
import com.thirdparty.ticketing.domain.seat.dto.SeatCreationElement;
import com.thirdparty.ticketing.domain.seat.dto.SeatCreationRequest;
import com.thirdparty.ticketing.domain.seat.repository.SeatGradeRepository;
import com.thirdparty.ticketing.domain.seat.repository.SeatRepository;
import com.thirdparty.ticketing.domain.zone.Zone;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

@DataJpaTest
class AdminSeatServiceTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private SeatRepository seatRepository;
    @Autowired
    private SeatGradeRepository seatGradeRepository;

    private AdminSeatService adminSeatService;

    @BeforeEach
    void setUpExternal() {
        adminSeatService = new AdminSeatService(seatRepository, seatGradeRepository);
    }

    @Nested
    @DisplayName("createSeats 메서드를 호출할 때")
    class CreateSeatsTest {
        private Performance performance;
        private Zone zone;
        private SeatGrade seatGrade1;
        private SeatGrade seatGrade2;
        private SeatCreationRequest seatCreationRequest;

        @BeforeEach
        void setUp() {
            setUpPerformance();
            setUpZone();
            setUpSeatGrades();

            SeatCreationElement seat1 = new SeatCreationElement();
            seat1.setSeatCode("A01");
            seat1.setGradeName(seatGrade1.getGradeName());
            SeatCreationElement seat2 = new SeatCreationElement();
            seat2.setSeatCode("B01");
            seat2.setGradeName(seatGrade2.getGradeName());

            seatCreationRequest = new SeatCreationRequest();
            seatCreationRequest.setSeats(List.of(
                    seat1, seat2
            ));
        }

        private void setUpPerformance() {
            Performance performance = Performance.builder()
                    .performanceName("공연")
                    .performancePlace("장소")
                    .performanceShowtime(ZonedDateTime.of(2024, 8, 23, 14, 30, 0, 0, ZoneId.of("Asia/Seoul")))
                    .build();
            testEntityManager.persistAndFlush(performance);
            this.performance = performance;
        }

        private void setUpZone() {
            Zone zone = Zone.builder()
                    .zoneName("VIP")
                    .performance(performance)
                    .build();
            testEntityManager.persistAndFlush(zone);
            this.zone = zone;
        }

        private void setUpSeatGrades() {
            SeatGrade seatGrade1 = SeatGrade.builder()
                    .performance(performance)
                    .price(10000L)
                    .gradeName("Grade1")
                    .build();
            SeatGrade seatGrade2 = SeatGrade.builder()
                    .performance(performance)
                    .price(20000L)
                    .gradeName("Grade2")
                    .build();
            testEntityManager.persistAndFlush(seatGrade1);
            testEntityManager.persistAndFlush(seatGrade2);
            this.seatGrade1 = seatGrade1;
            this.seatGrade2 = seatGrade2;
        }

        @Test
        @DisplayName("좌석이 성공적으로 생성된다.")
        void createSeats_Success() {
            // Given
            // No additional setup required

            // When
            adminSeatService.createSeats(performance.getPerformanceId(), zone.getZoneId(), seatCreationRequest);

            // Then
            List<Seat> seats = seatRepository.findAll();
            Seat seat1 = seats.get(0);
            Seat seat2 = seats.get(1);

            assertThat(seats).hasSize(2);
            assertThat(seat1.getSeatCode()).isEqualTo("A01");
            assertThat(seat1.getZone().getZoneId()).isEqualTo(zone.getZoneId());
            assertThat(seat1.getSeatGrade().getSeatGradeId()).isEqualTo(seatGrade1.getSeatGradeId());

            assertThat(seat2.getSeatCode()).isEqualTo("B01");
            assertThat(seat2.getZone().getZoneId()).isEqualTo(zone.getZoneId());
            assertThat(seat2.getSeatGrade().getSeatGradeId()).isEqualTo(seatGrade2.getSeatGradeId());
        }
    }
}
