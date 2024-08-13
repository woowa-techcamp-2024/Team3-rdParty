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

import com.thirdparty.ticketing.domain.ItemResult;
import com.thirdparty.ticketing.domain.member.Member;
import com.thirdparty.ticketing.domain.member.MemberRole;
import com.thirdparty.ticketing.domain.performance.Performance;
import com.thirdparty.ticketing.domain.seat.Seat;
import com.thirdparty.ticketing.domain.seat.SeatGrade;
import com.thirdparty.ticketing.domain.seat.dto.response.SeatElement;
import com.thirdparty.ticketing.domain.seat.repository.SeatRepository;
import com.thirdparty.ticketing.domain.zone.Zone;
import com.thirdparty.ticketing.domain.zone.repository.ZoneRepository;

@DataJpaTest
public class SeatServiceTest {
    @Autowired private TestEntityManager testEntityManager;
    @Autowired private SeatRepository seatRepository;
    @Autowired private ZoneRepository zoneRepository;

    private SeatService seatService;

    private Member member;
    private Performance performance;
    private Zone zone;
    private SeatGrade seatGrade1;
    private SeatGrade seatGrade2;

    @BeforeEach
    void setUpBase() {
        seatService = new SeatService(zoneRepository, seatRepository);
        setUpMember();
        setUpPerformance();
        setUpZone();
        setUpSeatGrades();
    }

    private void setUpMember() {
        Member member =
                Member.builder()
                        .email("test@gmail.com")
                        .password("testpassword")
                        .memberRole(MemberRole.USER)
                        .build();

        testEntityManager.persist(member);
        this.member = member;
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

    @Nested
    @DisplayName("getZones 메서드를 호출할 때")
    class GetZonesTest {
        private Seat seat1;
        private Seat seat2;

        @BeforeEach
        void setUp() {
            seat1 =
                    Seat.builder()
                            .member(member)
                            .seatCode("A01")
                            .zone(zone)
                            .seatGrade(seatGrade1)
                            .build();

            seat2 =
                    Seat.builder()
                            .member(member)
                            .seatCode("A01")
                            .zone(zone)
                            .seatGrade(seatGrade2)
                            .build();
        }

        @Test
        @DisplayName("좌석 등급이 성공적으로 생성된다.")
        void getZones_success() {
            // Given
            testEntityManager.persistAndFlush(seat1);
            testEntityManager.persistAndFlush(seat2);

            // When
            ItemResult<SeatElement> seats = seatService.getSeats(zone.getZoneId());

            // Then
            List<SeatElement> seatElements = seats.getItems();
            SeatElement seatElement1 = seatElements.get(0);
            SeatElement seatElement2 = seatElements.get(1);

            assertThat(seatElements).hasSize(2);
            assertSeatElement(seatElement1, seat1);
            assertSeatElement(seatElement2, seat2);
        }

        private void assertSeatElement(SeatElement seatElement, Seat expectedSeat) {
            assertThat(seatElement.getSeatId()).isEqualTo(expectedSeat.getSeatId());
            assertThat(seatElement.getSeatCode()).isEqualTo(expectedSeat.getSeatCode());
        }
    }
}
