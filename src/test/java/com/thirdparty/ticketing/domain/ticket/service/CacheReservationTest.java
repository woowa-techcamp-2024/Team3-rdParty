package com.thirdparty.ticketing.domain.ticket.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.thirdparty.ticketing.support.BaseIntegrationTest;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import com.thirdparty.ticketing.domain.common.LettuceRepository;
import com.thirdparty.ticketing.domain.common.TicketingException;
import com.thirdparty.ticketing.domain.member.Member;
import com.thirdparty.ticketing.domain.member.MemberRole;
import com.thirdparty.ticketing.domain.member.repository.MemberRepository;
import com.thirdparty.ticketing.domain.performance.Performance;
import com.thirdparty.ticketing.domain.performance.repository.PerformanceRepository;
import com.thirdparty.ticketing.domain.seat.Seat;
import com.thirdparty.ticketing.domain.seat.SeatGrade;
import com.thirdparty.ticketing.domain.seat.SeatStatus;
import com.thirdparty.ticketing.domain.seat.repository.SeatGradeRepository;
import com.thirdparty.ticketing.domain.seat.repository.SeatRepository;
import com.thirdparty.ticketing.domain.ticket.dto.request.SeatSelectionRequest;
import com.thirdparty.ticketing.domain.zone.Zone;
import com.thirdparty.ticketing.domain.zone.repository.ZoneRepository;
import com.thirdparty.ticketing.support.TestContainerStarter;

public class CacheReservationTest extends BaseIntegrationTest {

    @Autowired private SeatRepository seatRepository;

    @Autowired private MemberRepository memberRepository;

    @Autowired private ZoneRepository zoneRepository;

    @Autowired private SeatGradeRepository seatGradeRepository;

    @Autowired private PerformanceRepository performanceRepository;

    @Autowired private LettuceRepository lettuceRepository;

    @Autowired private RedissonClient redissonClient;

    @Autowired
    @Qualifier("lettuceReservationServiceProxy")
    private ReservationService lettuceCacheTicketService;

    @Autowired
    @Qualifier("redissonReservationServiceProxy")
    private ReservationService redissonReservationServiceProxy;

    private List<Member> members;
    private Seat seat;
    private Zone zone;
    private SeatGrade seatGrade;
    private Performance performance;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        members =
                memberRepository.saveAllAndFlush(
                        List.of(
                                new Member("member1@example.com", "password", MemberRole.USER),
                                new Member("member2@example.com", "password", MemberRole.USER),
                                new Member("member3@example.com", "password", MemberRole.USER),
                                new Member("member4@example.com", "password", MemberRole.USER),
                                new Member("member5@example.com", "password", MemberRole.USER)));

        performance =
                performanceRepository.saveAndFlush(
                        new Performance(
                                1L,
                                "Phantom of the Opera",
                                "Broadway Theater",
                                ZonedDateTime.now().plusDays(10)));

        seatGrade =
                seatGradeRepository.saveAndFlush(new SeatGrade(1L, performance, 20000L, "Regular"));
        zone = zoneRepository.saveAndFlush(new Zone(1L, performance, "R"));

        seat =
                seatRepository.saveAndFlush(
                        Seat.builder()
                                .zone(zone)
                                .seatGrade(seatGrade)
                                .seatCode("R")
                                .seatStatus(SeatStatus.SELECTABLE)
                                .build());
    }

    @AfterEach
    void breakUp() {
        seatRepository.deleteAll();
        zoneRepository.deleteAll();
        seatGradeRepository.deleteAll();
        performanceRepository.deleteAll();
        memberRepository.deleteAll();
    }

    @Test
    public void testConcurrentSeatSelectionWithLettuce() throws InterruptedException {
        runConcurrentSeatSelectionTest(lettuceCacheTicketService);
    }

    @Test
    public void testConcurrentSeatSelectionWithRedisson() throws InterruptedException {
        runConcurrentSeatSelectionTest(redissonReservationServiceProxy);
    }

    private void runConcurrentSeatSelectionTest(ReservationService reservationServiceProxy)
            throws InterruptedException {

        int threadCount = members.size();
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        AtomicInteger successfulSelections = new AtomicInteger(0);
        AtomicInteger failureSelections = new AtomicInteger(0);

        for (Member member : members) {
            // 각 멤버에 대해 작업을 스레드 풀에 제출
            executorService.submit(
                    () -> {
                        try {
                            // 스레드 풀에서 병렬로 실행되는 작업
                            SeatSelectionRequest seatSelectionRequest = new SeatSelectionRequest();
                            seatSelectionRequest.setSeatId(seat.getSeatId());
                            reservationServiceProxy.selectSeat(
                                    member.getEmail(), seatSelectionRequest);
                            successfulSelections.incrementAndGet();
                        } catch (TicketingException e) {
                            failureSelections.incrementAndGet();
                        } catch (Exception e) {
                        } finally {
                            // latch 카운트 감소, 스레드 완료 시 호출
                            latch.countDown();
                        }
                    });
        }

        latch.await();

        Seat reservedSeat = seatRepository.findById(seat.getSeatId()).orElseThrow();
        assertThat(reservedSeat.getMember()).isNotNull();
        assertThat(successfulSelections.get()).isEqualTo(1);
        assertThat(failureSelections.get()).isEqualTo(4);
    }
}
