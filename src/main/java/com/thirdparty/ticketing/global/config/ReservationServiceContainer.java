package com.thirdparty.ticketing.global.config;

import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.thirdparty.ticketing.domain.common.LettuceRepository;
import com.thirdparty.ticketing.domain.member.repository.MemberRepository;
import com.thirdparty.ticketing.domain.payment.PaymentProcessor;
import com.thirdparty.ticketing.domain.seat.repository.SeatRepository;
import com.thirdparty.ticketing.domain.ticket.policy.LockSeatStrategy;
import com.thirdparty.ticketing.domain.ticket.policy.NaiveSeatStrategy;
import com.thirdparty.ticketing.domain.ticket.policy.OptimisticLockSeatStrategy;
import com.thirdparty.ticketing.domain.ticket.policy.PessimisticLockSeatStrategy;
import com.thirdparty.ticketing.domain.ticket.service.*;

@Configuration
public class ReservationServiceContainer {
    @Bean
    public ReservationServiceProxy redissonReservationServiceProxy(
            RedissonClient redissonClient,
            ReservationTransactionService cacheReservationTransactionService) {
        return new RedissonReservationServiceProxy(
                redissonClient, cacheReservationTransactionService);
    }

    @Bean
    public ReservationServiceProxy lettuceReservationServiceProxy(
            LettuceRepository lettuceRepository,
            ReservationTransactionService cacheReservationTransactionService) {
        return new LettuceReservationServiceProxy(
                lettuceRepository, cacheReservationTransactionService);
    }

    @Bean
    public ReservationTransactionService cacheReservationTransactionService(
            PaymentProcessor paymentProcessor,
            MemberRepository memberRepository,
            SeatRepository seatRepository) {
        LockSeatStrategy lockSeatStrategy = new NaiveSeatStrategy(seatRepository);
        return new ReservationTransactionService(
                memberRepository, paymentProcessor, lockSeatStrategy);
    }

    @Bean
    public ReservationTransactionService persistenceOptimisticReservationService(
            PaymentProcessor paymentProcessor,
            MemberRepository memberRepository,
            SeatRepository seatRepository) {
        LockSeatStrategy lockSeatStrategy = new OptimisticLockSeatStrategy(seatRepository);
        return new ReservationTransactionService(
                memberRepository, paymentProcessor, lockSeatStrategy);
    }

    @Bean
    public ReservationTransactionService persistencePessimisticReservationService(
            PaymentProcessor paymentProcessor,
            MemberRepository memberRepository,
            SeatRepository seatRepository) {
        LockSeatStrategy lockSeatStrategy = new PessimisticLockSeatStrategy(seatRepository);
        return new ReservationTransactionService(
                memberRepository, paymentProcessor, lockSeatStrategy);
    }
}
