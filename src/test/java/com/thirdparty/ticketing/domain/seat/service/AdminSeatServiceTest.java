package com.thirdparty.ticketing.domain.seat.service;

import static org.assertj.core.api.Assertions.assertThat;

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

import com.thirdparty.ticketing.domain.performance.Performance;
import com.thirdparty.ticketing.domain.performance.repository.PerformanceRepository;
import com.thirdparty.ticketing.domain.seat.Seat;
import com.thirdparty.ticketing.domain.seat.SeatGrade;
import com.thirdparty.ticketing.domain.seat.dto.request.SeatCreationElement;
import com.thirdparty.ticketing.domain.seat.dto.request.SeatCreationRequest;
import com.thirdparty.ticketing.domain.seat.dto.request.SeatGradeCreationElement;
import com.thirdparty.ticketing.domain.seat.dto.request.SeatGradeCreationRequest;
import com.thirdparty.ticketing.domain.seat.repository.SeatGradeRepository;
import com.thirdparty.ticketing.domain.seat.repository.SeatRepository;
import com.thirdparty.ticketing.domain.zone.Zone;
import com.thirdparty.ticketing.domain.zone.repository.ZoneRepository;

@DataJpaTest
class AdminSeatServiceTest {
    @Autowired private TestEntityManager testEntityManager;
    @Autowired private SeatRepository seatRepository;
    @Autowired private SeatGradeRepository seatGradeRepository;
    @Autowired private ZoneRepository zoneRepository;
    @Autowired private PerformanceRepository performanceRepository;

    private AdminSeatService adminSeatService;

    private Performance performance;
    private Zone zone;

    @BeforeEach
    void setUpBase() {
        adminSeatService =
                new AdminSeatService(
                        seatRepository, seatGradeRepository, performanceRepository, zoneRepository);
        setUpPerformance();
        setUpZone();
    }

    private void setUpPerformance() {
        Performance performance =
                Performance.builder()
                        .performanceName("공연")
                        .performancePlace("장소")
                        .performanceShowtime(
                                ZonedDateTime.of(
                                        2024, 8, 23, 14, 30, 0, 0, ZoneId.of("Asia/Seoul")))
                        .build();
        testEntityManager.persistAndFlush(performance);
        this.performance = performance;
    }

    private void setUpZone() {
        Zone zone = Zone.builder().zoneName("VIP").performance(performance).build();
        testEntityManager.persistAndFlush(zone);
        this.zone = zone;
    }

    @Nested
    @DisplayName("createSeatGrades 메서드를 호출할 때")
    class CreateSeatGradesTest {

        @Test
        @DisplayName("좌석 등급이 성공적으로 생성된다.")
        void createSeats_Success() {
            // Given
            SeatGradeCreationRequest seatGradeCreationRequest = makeRequest();

            // When
            adminSeatService.createSeatGrades(
                    performance.getPerformanceId(), seatGradeCreationRequest);

            // Then
            List<SeatGrade> seatGrades = seatGradeRepository.findAll();
            SeatGrade seatGrade1 = seatGrades.get(0);
            SeatGrade seatGrade2 = seatGrades.get(1);

            assertThat(seatGrades).hasSize(2);
            assertThat(seatGrade1.getPerformance().getPerformanceId())
                    .isEqualTo(performance.getPerformanceId());
            assertThat(seatGrade1.getGradeName()).isEqualTo("Grade1");
            assertThat(seatGrade1.getPrice()).isEqualTo(10000L);

            assertThat(seatGrade2.getPerformance().getPerformanceId())
                    .isEqualTo(performance.getPerformanceId());
            assertThat(seatGrade2.getGradeName()).isEqualTo("Grade2");
            assertThat(seatGrade2.getPrice()).isEqualTo(20000L);
        }

        private SeatGradeCreationRequest makeRequest() {
            SeatGradeCreationRequest request = new SeatGradeCreationRequest();

            SeatGradeCreationElement seatGrade1 = new SeatGradeCreationElement();
            seatGrade1.setGradeName("Grade1");
            seatGrade1.setPrice(10000L);
            SeatGradeCreationElement seatGrade2 = new SeatGradeCreationElement();
            seatGrade2.setGradeName("Grade2");
            seatGrade2.setPrice(20000L);

            request.setSeatGrades(List.of(seatGrade1, seatGrade2));
            return request;
        }
    }

    @Nested
    @DisplayName("createSeats 메서드를 호출할 때")
    class CreateSeatsTest {
        private SeatGrade seatGrade1;
        private SeatGrade seatGrade2;

        @BeforeEach
        void setUp() {
            setUpSeatGrades();
        }

        private void setUpSeatGrades() {
            SeatGrade seatGrade1 =
                    SeatGrade.builder()
                            .performance(performance)
                            .price(10000L)
                            .gradeName("Grade1")
                            .build();
            SeatGrade seatGrade2 =
                    SeatGrade.builder()
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
            SeatCreationRequest seatCreationRequest = makeRequest();

            // When
            adminSeatService.createSeats(
                    performance.getPerformanceId(), zone.getZoneId(), seatCreationRequest);

            // Then
            List<Seat> seats = seatRepository.findAll();
            Seat seat1 = seats.get(0);
            Seat seat2 = seats.get(1);

            assertThat(seats).hasSize(2);
            assertThat(seat1.getSeatCode()).isEqualTo("A01");
            assertThat(seat1.getZone().getZoneId()).isEqualTo(zone.getZoneId());
            assertThat(seat1.getSeatGrade().getSeatGradeId())
                    .isEqualTo(seatGrade1.getSeatGradeId());

            assertThat(seat2.getSeatCode()).isEqualTo("B01");
            assertThat(seat2.getZone().getZoneId()).isEqualTo(zone.getZoneId());
            assertThat(seat2.getSeatGrade().getSeatGradeId())
                    .isEqualTo(seatGrade2.getSeatGradeId());
        }

        private SeatCreationRequest makeRequest() {
            SeatCreationRequest request = new SeatCreationRequest();

            SeatCreationElement seat1 = new SeatCreationElement();
            seat1.setSeatCode("A01");
            seat1.setGradeId(seatGrade1.getSeatGradeId());
            SeatCreationElement seat2 = new SeatCreationElement();
            seat2.setSeatCode("B01");
            seat2.setGradeId(seatGrade2.getSeatGradeId());

            request.setSeats(List.of(seat1, seat2));

            return request;
        }
    }
}
