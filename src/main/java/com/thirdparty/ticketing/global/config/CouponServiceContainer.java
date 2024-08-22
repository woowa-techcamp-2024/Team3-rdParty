package com.thirdparty.ticketing.global.config;

import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.thirdparty.ticketing.domain.common.LettuceRepository;
import com.thirdparty.ticketing.domain.coupon.repository.CouponRepository;
import com.thirdparty.ticketing.domain.coupon.repository.MemberCouponRepository;
import com.thirdparty.ticketing.domain.coupon.service.CouponTransactionalService;
import com.thirdparty.ticketing.domain.coupon.service.proxy.CouponServiceProxy;
import com.thirdparty.ticketing.domain.coupon.service.proxy.LettuceCouponServiceProxy;
import com.thirdparty.ticketing.domain.coupon.service.proxy.RedissonCouponServiceProxy;
import com.thirdparty.ticketing.domain.coupon.service.strategy.LockCouponStrategy;
import com.thirdparty.ticketing.domain.coupon.service.strategy.NaiveCouponStrategy;
import com.thirdparty.ticketing.domain.coupon.service.strategy.OptimisticCouponStrategy;
import com.thirdparty.ticketing.domain.member.repository.MemberRepository;

@Configuration
public class CouponServiceContainer {

    @Bean
    @Primary
    public CouponServiceProxy redissonCouponServiceProxy(
            RedissonClient redissonClient,
            @Qualifier("cacheCouponTransactionService")
                    CouponTransactionalService cacheCouponTransactionService) {
        return new RedissonCouponServiceProxy(redissonClient, cacheCouponTransactionService);
    }

    @Bean
    public CouponServiceProxy lettuceCouponServiceProxy(
            LettuceRepository lettuceRepository,
            @Qualifier("cacheCouponTransactionService")
                    CouponTransactionalService cacheCouponTransactionService) {
        return new LettuceCouponServiceProxy(lettuceRepository, cacheCouponTransactionService);
    }

    @Bean
    public CouponTransactionalService cacheCouponTransactionService(
            MemberRepository memberRepository,
            CouponRepository couponRepository,
            MemberCouponRepository memberCouponRepository) {
        NaiveCouponStrategy lockCouponStrategy = new NaiveCouponStrategy(couponRepository);
        return new CouponTransactionalService(
                memberRepository, lockCouponStrategy, memberCouponRepository);
    }

    @Bean
    public CouponTransactionalService persistenceOptimisticReservationService(
            MemberRepository memberRepository,
            CouponRepository couponRepository,
            MemberCouponRepository memberCouponRepository) {
        LockCouponStrategy lockCouponStrategy = new OptimisticCouponStrategy(couponRepository);
        return new CouponTransactionalService(
                memberRepository, lockCouponStrategy, memberCouponRepository);
    }

    @Bean
    public CouponTransactionalService persistencePessimisticReservationService(
            MemberRepository memberRepository,
            CouponRepository couponRepository,
            MemberCouponRepository memberCouponRepository) {
        LockCouponStrategy lockCouponStrategy = new OptimisticCouponStrategy(couponRepository);
        return new CouponTransactionalService(
                memberRepository, lockCouponStrategy, memberCouponRepository);
    }
}
