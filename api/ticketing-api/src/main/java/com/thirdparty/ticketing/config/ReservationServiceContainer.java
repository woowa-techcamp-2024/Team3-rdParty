package com.thirdparty.ticketing.config;

import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.thirdparty.ticketing.event.EventPublisher;
import com.thirdparty.ticketing.jpa.member.repository.MemberRepository;
import com.thirdparty.ticketing.jpa.payment.PaymentProcessor;
import com.thirdparty.ticketing.jpa.seat.repository.SeatRepository;
import com.thirdparty.ticketing.jpa.ticket.repository.TicketRepository;
import com.thirdparty.ticketing.redis.repository.LettuceSeatLockRepository;
import com.thirdparty.ticketing.ticket.service.ReservationManager;
import com.thirdparty.ticketing.ticket.service.ReservationService;
import com.thirdparty.ticketing.ticket.service.ReservationTransactionService;
import com.thirdparty.ticketing.ticket.service.proxy.OptimisticReservationServiceProxy;
import com.thirdparty.ticketing.ticket.service.proxy.PessimisticReservationServiceProxy;
import com.thirdparty.ticketing.ticket.service.proxy.RedissonReservationServiceProxy;
import com.thirdparty.ticketing.ticket.service.strategy.LockSeatStrategy;
import com.thirdparty.ticketing.ticket.service.strategy.NaiveSeatStrategy;
import com.thirdparty.ticketing.ticket.service.strategy.OptimisticLockSeatStrategy;
import com.thirdparty.ticketing.ticket.service.strategy.PessimisticLockSeatStrategy;

@Configuration
public class ReservationServiceContainer {
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
            LettuceSeatLockRepository lettuceRepository,
            @Qualifier("cacheReservationTransactionService")
                    ReservationTransactionService cacheReservationTransactionService) {
        return new com.thirdparty.ticketing.domain.ticket.service.proxy
                .LettuceReservationServiceProxy(
                lettuceRepository, cacheReservationTransactionService);
    }

    @Primary
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
                reservationManager);
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
                reservationManager);
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
                reservationManager);
    }
}
