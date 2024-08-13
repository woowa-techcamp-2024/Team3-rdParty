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
import com.thirdparty.ticketing.domain.seat.Seat;
import com.thirdparty.ticketing.domain.seat.dto.SeatCreationElement;
import com.thirdparty.ticketing.domain.seat.dto.SeatCreationRequest;
import com.thirdparty.ticketing.domain.seat.repository.SeatRepository;
import com.thirdparty.ticketing.domain.zone.Zone;

@DataJpaTest
class AdminSeatServiceTest {

    @Autowired private TestEntityManager testEntityManager;

    @Autowired private SeatRepository seatRepository;

    private AdminSeatService adminSeatService;

    @BeforeEach
    void setUpExternal() {
        adminSeatService = new AdminSeatService(seatRepository);
    }

    @Nested
    @DisplayName("createSeats 메서드를 호출할 때")
    class CreateSeatsTest {

        private Long zoneId;
        private SeatCreationRequest seatCreationRequest;

        @BeforeEach
        void setUp() {
            // Create and save a Zone entity
            Performance performance =
                    Performance.builder()
                            .performanceName("공연")
                            .performancePlace("장소")
                            .performanceShowtime(
                                    ZonedDateTime.of(
                                            2024, 8, 23, 14, 30, 0, 0, ZoneId.of("Asia/Seoul")))
                            .build();
            Zone zone = Zone.builder().zoneName("VIP").performance(performance).build();
            testEntityManager.persistAndFlush(performance);
            testEntityManager.persistAndFlush(zone);
            zoneId = zone.getZoneId();

            SeatCreationElement seat1 = new SeatCreationElement();
            seat1.setSeatCode("A01");
            SeatCreationElement seat2 = new SeatCreationElement();
            seat2.setSeatCode("B01");

            seatCreationRequest = new SeatCreationRequest();
            seatCreationRequest.setSeats(List.of(seat1, seat2));
        }

        @Test
        @DisplayName("좌석이 성공적으로 생성된다.")
        void createSeats_Success() {
            // Given
            // No additional setup required

            // When
            adminSeatService.createSeats(zoneId, seatCreationRequest);

            // Then
            List<Seat> seats = seatRepository.findAll();
            assertThat(seats).hasSize(2);
            Seat seat1 = seats.get(0);
            assertThat(seat1.getSeatCode()).isEqualTo("A01");
            assertThat(seats.get(1).getSeatCode()).isEqualTo("B01");
            assertThat(seat1.getZone().getZoneId()).isEqualTo(zoneId);
        }
    }
}
