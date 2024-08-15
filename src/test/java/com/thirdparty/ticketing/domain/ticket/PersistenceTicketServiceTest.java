package com.thirdparty.ticketing.domain.ticket;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import javax.sql.DataSource;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

import com.thirdparty.ticketing.domain.member.repository.MemberRepository;
import com.thirdparty.ticketing.domain.payment.SimulatedPaymentProcessor;
import com.thirdparty.ticketing.domain.seat.repository.SeatRepository;
import com.thirdparty.ticketing.domain.ticket.dto.SeatSelectionRequest;
import com.thirdparty.ticketing.domain.ticket.policy.OptimisticLockSeatStrategy;
import com.thirdparty.ticketing.domain.ticket.repository.TicketRepository;
import com.thirdparty.ticketing.domain.ticket.service.PersistenceTicketService;
import com.thirdparty.ticketing.domain.ticket.service.TicketService;

@DataJpaTest
@Import({
        PersistenceTicketService.class,
        SimulatedPaymentProcessor.class,
        OptimisticLockSeatStrategy.class
})
public class PersistenceTicketServiceTest {
    private static final Logger log = LoggerFactory.getLogger(PersistenceTicketServiceTest.class);
    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private DataSource dataSource;
    @Autowired
    private SeatRepository seatRepository;
    @Autowired
    private TicketRepository ticketRepository;
    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private TicketService ticketService;

    private String memberEmail = "test@gmail.com";
    private Long seatId = 1L;

    @Nested
    @DisplayName("좌석을 한 번에 여러개 선택할 때")
    @Sql(
            scripts = "/db/select-seat-test.sql",
            config = @SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED))
    class SelectSeatConcurrencyTest {

        @Test
        @DisplayName("다른 스레드에서 테스트 데이터를 볼 수 있는지 확인한다")
        void select_otherThread() throws InterruptedException {
            Long seatId = 1L;

            ExecutorService executor = Executors.newFixedThreadPool(2);

            executor.execute(
                    () -> {
                        seatRepository.findById(seatId).orElseThrow();
                    });

            executor.shutdown();
            executor.awaitTermination(10, TimeUnit.SECONDS);
        }

        @Test
        @DisplayName("여러개의 동시 요청 중 한 명만 좌석을 성공적으로 선택해야 한다.")
        void selectSeat_ConcurrencyTest() throws InterruptedException {
            // Given
            int numRequests = 2000;
            CountDownLatch latch = new CountDownLatch(1);
            ExecutorService executor = Executors.newFixedThreadPool(numRequests);

            AtomicInteger successfulSelections = new AtomicInteger(0);
            AtomicInteger failedSelections = new AtomicInteger(0);

            // when
            IntStream.range(0, numRequests)
                    .forEach(
                            i ->
                                    executor.submit(
                                            () ->
                                                    selectSeatTask(
                                                            latch,
                                                            seatId,
                                                            successfulSelections,
                                                            failedSelections)));

            latch.countDown(); // 모든 스레드가 동시에 실행되도록 설정

            executor.shutdown();
            executor.awaitTermination(10, TimeUnit.SECONDS);

            // Then
            assertThat(successfulSelections.get()).isEqualTo(1);
            assertThat(failedSelections.get()).isEqualTo(numRequests - 1);
        }

        private void selectSeatTask(
                CountDownLatch latch,
                Long seatId,
                AtomicInteger successfulSelections,
                AtomicInteger failedSelections) {

            setUpAuthentication();
            try {
                latch.await();
                try {
                    ticketService.selectSeat(memberEmail, new SeatSelectionRequest(seatId));
                    successfulSelections.incrementAndGet();
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    failedSelections.incrementAndGet();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        private void setUpAuthentication() {
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            Authentication authentication =
                    new UsernamePasswordAuthenticationToken(
                            "test@gmail.com", "testpassword", List.of());
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);
        }
    }
}
