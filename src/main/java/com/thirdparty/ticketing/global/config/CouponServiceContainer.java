package com.thirdparty.ticketing.global.config;

import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.thirdparty.ticketing.domain.common.LettuceRepository;
import com.thirdparty.ticketing.domain.coupon.repository.CouponMemberRepository;
import com.thirdparty.ticketing.domain.coupon.repository.CouponRepository;
import com.thirdparty.ticketing.domain.coupon.service.CouponService;
import com.thirdparty.ticketing.domain.coupon.service.CouponTransactionalService;
import com.thirdparty.ticketing.domain.coupon.service.proxy.LettuceCouponServiceProxy;
import com.thirdparty.ticketing.domain.coupon.service.proxy.OptimisticCouponServiceProxy;
import com.thirdparty.ticketing.domain.coupon.service.proxy.PessimisticCouponServiceProxy;
import com.thirdparty.ticketing.domain.coupon.service.proxy.RedissonCouponServiceProxy;
import com.thirdparty.ticketing.domain.coupon.service.strategy.LockCouponStrategy;
import com.thirdparty.ticketing.domain.coupon.service.strategy.NaiveCouponStrategy;
import com.thirdparty.ticketing.domain.coupon.service.strategy.OptimisticCouponStrategy;
import com.thirdparty.ticketing.domain.coupon.service.strategy.PessimisticCouponStrategy;
import com.thirdparty.ticketing.domain.member.repository.MemberRepository;

@Configuration
public class CouponServiceContainer {

    @Bean
    public CouponService redissonCouponServiceProxy(
            RedissonClient redissonClient,
            @Qualifier("cacheCouponTransactionService")
                    CouponTransactionalService cacheCouponTransactionService) {
        return new RedissonCouponServiceProxy(redissonClient, cacheCouponTransactionService);
    }

    @Bean
    public CouponService lettuceCouponServiceProxy(
            LettuceRepository lettuceRepository,
            @Qualifier("cacheCouponTransactionService")
                    CouponTransactionalService cacheCouponTransactionService) {
        return new LettuceCouponServiceProxy(lettuceRepository, cacheCouponTransactionService);
    }

    @Bean
    @Primary
    public CouponService optimisticCouponServiceProxy(
            @Qualifier("persistenceOptimisticCouponService")
                    CouponTransactionalService optimisticReservationService) {
        return new OptimisticCouponServiceProxy(optimisticReservationService);
    }

    @Bean
    public CouponService pessimisticCouponServiceProxy(
            @Qualifier("persistencePessimisticCouponService")
                    CouponTransactionalService pessimisticCouponService) {
        return new PessimisticCouponServiceProxy(pessimisticCouponService);
    }

    @Bean
    public CouponTransactionalService cacheCouponTransactionService(
            MemberRepository memberRepository,
            CouponRepository couponRepository,
            CouponMemberRepository couponMemberRepository) {
        NaiveCouponStrategy lockCouponStrategy = new NaiveCouponStrategy(couponRepository);
        return new CouponTransactionalService(
                memberRepository, lockCouponStrategy, couponMemberRepository);
    }

    @Bean
    public CouponTransactionalService persistenceOptimisticCouponService(
            MemberRepository memberRepository,
            CouponRepository couponRepository,
            CouponMemberRepository couponMemberRepository) {
        LockCouponStrategy lockCouponStrategy = new OptimisticCouponStrategy(couponRepository);
        return new CouponTransactionalService(
                memberRepository, lockCouponStrategy, couponMemberRepository);
    }

    @Bean
    public CouponTransactionalService persistencePessimisticCouponService(
            MemberRepository memberRepository,
            CouponRepository couponRepository,
            CouponMemberRepository couponMemberRepository) {
        LockCouponStrategy lockCouponStrategy = new PessimisticCouponStrategy(couponRepository);
        return new CouponTransactionalService(
                memberRepository, lockCouponStrategy, couponMemberRepository);
    }
}
