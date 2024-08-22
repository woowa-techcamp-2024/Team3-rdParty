package com.thirdparty.ticketing.domain.coupon.service.strategy;

import java.util.Optional;

import com.thirdparty.ticketing.domain.coupon.Coupon;
import com.thirdparty.ticketing.domain.coupon.repository.CouponRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class NaiveCouponStrategy implements LockCouponStrategy {
    private final CouponRepository couponRepository;

    @Override
    public Optional<Coupon> getCouponWithLock(Long couponId) {
        return couponRepository.findById(couponId);
    }
}
