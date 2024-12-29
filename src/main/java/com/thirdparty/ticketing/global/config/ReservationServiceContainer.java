package com.thirdparty.ticketing.global.config;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.thirdparty.ticketing.domain.common.EventPublisher;
import com.thirdparty.ticketing.domain.common.LettuceRepository;
import com.thirdparty.ticketing.domain.member.repository.MemberRepository;
import com.thirdparty.ticketing.domain.payment.PaymentProcessor;
import com.thirdparty.ticketing.domain.seat.repository.SeatRepository;
import com.thirdparty.ticketing.domain.ticket.repository.TicketRepository;
import com.thirdparty.ticketing.domain.ticket.service.*;
import com.thirdparty.ticketing.domain.ticket.service.proxy.*;
import com.thirdparty.ticketing.domain.ticket.service.strategy.LockSeatStrategy;
import com.thirdparty.ticketing.domain.ticket.service.strategy.NaiveSeatStrategy;
import com.thirdparty.ticketing.domain.ticket.service.strategy.OptimisticLockSeatStrategy;
import com.thirdparty.ticketing.domain.ticket.service.strategy.PessimisticLockSeatStrategy;

@Configuration
public class ReservationServiceContainer {

    @Value("${ticketing.reservation.release-delay-seconds}")
    private int reservationReleaseDelay;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(5);

    @Bean
    @Primary
    public ReservationService newRedisReservationService(
            MemberRepository memberRepository,
            SeatRepository seatRepository,
            StringRedisTemplate redisTemplate) {
        return new NewRedisReservationService(memberRepository, seatRepository, redisTemplate, reservationReleaseDelay);
    }


    @Bean
    public ReservationService redissonReservationServiceProxy(
            RedissonClient redissonClient,
            @Qualifier("cacheReservationTransactionService")
                    ReservationTransactionService cacheReservationTransactionService) {
        return new RedissonReservationServiceProxy(
                redissonClient, cacheReservationTransactionService);
    }

    @Bean
    public ReservationService lettuceReservationServiceProxy(
            LettuceRepository lettuceRepository,
            @Qualifier("cacheReservationTransactionService")
                    ReservationTransactionService cacheReservationTransactionService) {
        return new LettuceReservationServiceProxy(
                lettuceRepository, cacheReservationTransactionService);
    }

    @Bean
    ReservationService optimisticReservationServiceProxy(
            @Qualifier("persistenceOptimisticReservationService")
                    ReservationTransactionService persistenceOptimisticReservationService) {
        return new OptimisticReservationServiceProxy(persistenceOptimisticReservationService);
    }

    @Bean
    ReservationService pessimisticReservationServiceProxy(
            @Qualifier("persistencePessimisticReservationService")
                    ReservationTransactionService persistencePessimisticReservationService) {
        return new PessimisticReservationServiceProxy(persistencePessimisticReservationService);
    }

    @Bean
    public ReservationTransactionService cacheReservationTransactionService(
            TicketRepository ticketRepository,
            PaymentProcessor paymentProcessor,
            MemberRepository memberRepository,
            SeatRepository seatRepository,
            EventPublisher eventPublisher,
            ReservationManager reservationManager) {
        LockSeatStrategy lockSeatStrategy = new NaiveSeatStrategy(seatRepository);
        return new ReservationTransactionService(
                ticketRepository,
                memberRepository,
                paymentProcessor,
                lockSeatStrategy,
                eventPublisher,
                reservationManager,
                reservationReleaseDelay,
                scheduler
        );
    }

    @Bean
    public ReservationTransactionService persistenceOptimisticReservationService(
            TicketRepository ticketRepository,
            PaymentProcessor paymentProcessor,
            MemberRepository memberRepository,
            SeatRepository seatRepository,
            EventPublisher eventPublisher,
            ReservationManager reservationManager) {
        LockSeatStrategy lockSeatStrategy = new OptimisticLockSeatStrategy(seatRepository);
        return new ReservationTransactionService(
                ticketRepository,
                memberRepository,
                paymentProcessor,
                lockSeatStrategy,
                eventPublisher,
                reservationManager,
                reservationReleaseDelay,
                scheduler
        );
    }

    @Bean
    public ReservationTransactionService persistencePessimisticReservationService(
            TicketRepository ticketRepository,
            PaymentProcessor paymentProcessor,
            MemberRepository memberRepository,
            SeatRepository seatRepository,
            EventPublisher eventPublisher,
            ReservationManager reservationManager) {
        LockSeatStrategy lockSeatStrategy = new PessimisticLockSeatStrategy(seatRepository);
        return new ReservationTransactionService(
                ticketRepository,
                memberRepository,
                paymentProcessor,
                lockSeatStrategy,
                eventPublisher,
                reservationManager,
                reservationReleaseDelay,
                scheduler
        );
    }
}
