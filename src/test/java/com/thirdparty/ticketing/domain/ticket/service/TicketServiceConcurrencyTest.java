package com.thirdparty.ticketing.domain.ticket.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.thirdparty.ticketing.domain.common.LettuceRepository;
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

    @Autowired private LettuceCacheTicketService lettuceCacheTicketService;

    @Autowired private RedissonCacheTicketService redissonCacheTicketService;

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

        seat = spy(new Seat(1L, zone, seatGrade, null, "R", SeatStatus.SELECTABLE));

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
        // runConcurrentSeatSelectionTest(redissonCacheTicketService);
    }

    private void runConcurrentSeatSelectionTest(TicketService ticketService)
            throws InterruptedException {
        int threadCount = members.size();
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (Member member : members) {
            // 각 멤버에 대해 작업을 스레드 풀에 제출
            executorService.submit(
                    () -> {
                        try {
                            // 스레드 풀에서 병렬로 실행되는 작업
                            SeatSelectionRequest seatSelectionRequest =
                                    new SeatSelectionRequest(seat.getSeatId());
                            ticketService.selectSeat(member.getEmail(), seatSelectionRequest);
                        } catch (RuntimeException e) {
                            // 예외 발생 시 오류 로그 출력
                            System.err.println(
                                    "Exception occurred for member: "
                                            + member.getEmail()
                                            + " - "
                                            + e.getMessage());
                        } finally {
                            // latch 카운트 감소, 스레드 완료 시 호출
                            latch.countDown();
                        }
                    });
        }

        latch.await();

        Seat reservedSeat = seatRepository.findById(seat.getSeatId()).orElseThrow();
        assertNotNull(reservedSeat.getMember(), "Seat should be reserved by one member");

        // designateMember 메서드가 정확히 한 번 호출되었는지 확인
        verify(seat, times(5)).empty();
        verify(seat, times(1)).designateMember(any(Member.class));
    }
}
