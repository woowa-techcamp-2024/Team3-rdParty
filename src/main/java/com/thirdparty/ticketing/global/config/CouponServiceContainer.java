package com.thirdparty.ticketing.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.thirdparty.ticketing.domain.coupon.repository.CouponRepository;
import com.thirdparty.ticketing.domain.coupon.service.CouponTransactionalService;
import com.thirdparty.ticketing.domain.coupon.service.strategy.LockCouponStrategy;
import com.thirdparty.ticketing.domain.coupon.service.strategy.NaiveCouponStrategy;
import com.thirdparty.ticketing.domain.coupon.service.strategy.OptimisticCouponStrategy;
import com.thirdparty.ticketing.domain.member.repository.MemberRepository;

@Configuration
public class CouponServiceContainer {

    @Bean
    public CouponTransactionalService cacheCouponTransactionService(
            MemberRepository memberRepository, CouponRepository couponRepository) {
        NaiveCouponStrategy lockCouponStrategy = new NaiveCouponStrategy(couponRepository);
        return new CouponTransactionalService(memberRepository, lockCouponStrategy);
    }

    @Bean
    public CouponTransactionalService persistenceOptimisticReservationService(
            MemberRepository memberRepository, CouponRepository couponRepository) {
        LockCouponStrategy lockCouponStrategy = new OptimisticCouponStrategy(couponRepository);
        return new CouponTransactionalService(memberRepository, lockCouponStrategy);
    }

    @Bean
    public CouponTransactionalService persistencePessimisticReservationService(
            MemberRepository memberRepository, CouponRepository couponRepository) {
        LockCouponStrategy lockCouponStrategy = new OptimisticCouponStrategy(couponRepository);
        return new CouponTransactionalService(memberRepository, lockCouponStrategy);
    }
}
