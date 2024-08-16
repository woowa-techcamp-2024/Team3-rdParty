package com.thirdparty.ticketing.domain.ticket.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.thirdparty.ticketing.domain.common.LettuceRepository;
import com.thirdparty.ticketing.domain.common.TicketingException;
import com.thirdparty.ticketing.domain.member.Member;
import com.thirdparty.ticketing.domain.member.MemberRole;
import com.thirdparty.ticketing.domain.member.repository.MemberRepository;
import com.thirdparty.ticketing.domain.performance.Performance;
import com.thirdparty.ticketing.domain.seat.Seat;
import com.thirdparty.ticketing.domain.seat.SeatGrade;
import com.thirdparty.ticketing.domain.seat.SeatStatus;
import com.thirdparty.ticketing.domain.seat.repository.SeatRepository;
import com.thirdparty.ticketing.domain.ticket.dto.SeatSelectionRequest;
import com.thirdparty.ticketing.domain.zone.Zone;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class TicketServiceConcurrencyTest {

    @MockBean private SeatRepository seatRepository;

    @Autowired private MemberRepository memberRepository;

    @Autowired private LettuceRepository lettuceRepository;

    @Autowired private RedissonClient redissonClient;

    @Autowired
    @Qualifier("lettuceReservationServiceProxy")
    private ReservationServiceProxy lettuceCacheTicketService;

    @Autowired
    @Qualifier("reddisonReservationServiceProxy")
    private ReservationServiceProxy reddisonReservationServiceProxy;

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
                new Performance(
                        1L,
                        "Phantom of the Opera",
                        "Broadway Theater",
                        ZonedDateTime.now().plusDays(10));
        seatGrade = new SeatGrade(1L, performance, 20000L, "Regular");
        zone = new Zone(1L, performance, "R");

        seat =
                spy(
                        Seat.builder()
                                .seatId(1L)
                                .zone(zone)
                                .seatGrade(seatGrade)
                                .seatCode("R")
                                .seatStatus(SeatStatus.SELECTABLE)
                                .build());

        // Repository 모킹
        when(seatRepository.findById(seat.getSeatId())).thenReturn(Optional.of(seat));
    }

    @AfterEach
    void breakUp() {
        memberRepository.deleteAll();
    }

    @Test
    public void testConcurrentSeatSelectionWithLettuce() throws InterruptedException {
        runConcurrentSeatSelectionTest(lettuceCacheTicketService);
    }

    @Test
    public void testConcurrentSeatSelectionWithRedisson() throws InterruptedException {
        runConcurrentSeatSelectionTest(reddisonReservationServiceProxy);
    }

    private void runConcurrentSeatSelectionTest(ReservationServiceProxy reservationServiceProxy)
            throws InterruptedException {

        int threadCount = members.size();
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        AtomicInteger successfulSelections = new AtomicInteger(0);

        for (Member member : members) {
            // 각 멤버에 대해 작업을 스레드 풀에 제출
            executorService.submit(
                    () -> {
                        try {
                            // 스레드 풀에서 병렬로 실행되는 작업
                            SeatSelectionRequest seatSelectionRequest =
                                    new SeatSelectionRequest(seat.getSeatId());
                            reservationServiceProxy.selectSeat(
                                    member.getEmail(), seatSelectionRequest);
                            successfulSelections.incrementAndGet();
                        } catch (TicketingException e) {
                            // 예외 발생 시 오류 로그 출력
                            System.out.println("Error: " + e.getMessage());
                        } catch (Exception e) {
                            System.out.println("NOT DEFINED ERROR" + e.getMessage());
                        } finally {
                            // latch 카운트 감소, 스레드 완료 시 호출
                            latch.countDown();
                        }
                    });
        }

        latch.await();

        Seat reservedSeat = seatRepository.findById(seat.getSeatId()).orElseThrow();
        assertThat(reservedSeat.getMember()).isNotNull();
        System.out.println(reservedSeat.getMember().getEmail());
        // designateMember 메서드가 정확히 한 번 호출되었는지 확인
        verify(seat, times(5)).assignByMember(any(Member.class));
        Assertions.assertThat(successfulSelections.get()).isEqualTo(1);
    }
}
