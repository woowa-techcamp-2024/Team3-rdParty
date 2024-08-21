package com.thirdparty.ticketing.global.config;

import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.thirdparty.ticketing.domain.common.EventPublisher;
import com.thirdparty.ticketing.domain.common.LettuceRepository;
import com.thirdparty.ticketing.domain.member.repository.MemberRepository;
import com.thirdparty.ticketing.domain.payment.PaymentProcessor;
import com.thirdparty.ticketing.domain.seat.repository.SeatRepository;
import com.thirdparty.ticketing.domain.ticket.service.*;
import com.thirdparty.ticketing.domain.ticket.service.proxy.*;
import com.thirdparty.ticketing.domain.ticket.service.strategy.LockSeatStrategy;
import com.thirdparty.ticketing.domain.ticket.service.strategy.NaiveSeatStrategy;
import com.thirdparty.ticketing.domain.ticket.service.strategy.OptimisticLockSeatStrategy;
import com.thirdparty.ticketing.domain.ticket.service.strategy.PessimisticLockSeatStrategy;

@Configuration
public class ReservationServiceContainer {
    @Bean
    public ReservationService redissonReservationServiceProxy(
            RedissonClient redissonClient,
            ReservationTransactionService cacheReservationTransactionService) {
        return new RedissonReservationServiceProxy(
                redissonClient, cacheReservationTransactionService);
    }

    @Bean
    public ReservationService lettuceReservationServiceProxy(
            LettuceRepository lettuceRepository,
            ReservationTransactionService cacheReservationTransactionService) {
        return new LettuceReservationServiceProxy(
                lettuceRepository, cacheReservationTransactionService);
    }

    @Primary
    @Bean
    ReservationService optimisticReservationServiceProxy(
            ReservationTransactionService persistenceOptimisticReservationService) {
        return new OptimisticReservationServiceProxy(persistenceOptimisticReservationService);
    }

    @Bean
    ReservationService pessimisticReservationServiceProxy(
            ReservationTransactionService persistencePessimisticReservationService) {
        return new PessimisticReservationServiceProxy(persistencePessimisticReservationService);
    }

    @Bean
    public ReservationTransactionService cacheReservationTransactionService(
            PaymentProcessor paymentProcessor,
            MemberRepository memberRepository,
            SeatRepository seatRepository,
            EventPublisher eventPublisher) {
        LockSeatStrategy lockSeatStrategy = new NaiveSeatStrategy(seatRepository);
        return new ReservationTransactionService(
                memberRepository, paymentProcessor, lockSeatStrategy, eventPublisher);
    }

    @Bean
    public ReservationTransactionService persistenceOptimisticReservationService(
            PaymentProcessor paymentProcessor,
            MemberRepository memberRepository,
            SeatRepository seatRepository,
            EventPublisher eventPublisher) {
        LockSeatStrategy lockSeatStrategy = new OptimisticLockSeatStrategy(seatRepository);
        return new ReservationTransactionService(
                memberRepository, paymentProcessor, lockSeatStrategy, eventPublisher);
    }

    @Bean
    public ReservationTransactionService persistencePessimisticReservationService(
            PaymentProcessor paymentProcessor,
            MemberRepository memberRepository,
            SeatRepository seatRepository,
            EventPublisher eventPublisher) {
        LockSeatStrategy lockSeatStrategy = new PessimisticLockSeatStrategy(seatRepository);
        return new ReservationTransactionService(
                memberRepository, paymentProcessor, lockSeatStrategy, eventPublisher);
    }
}
