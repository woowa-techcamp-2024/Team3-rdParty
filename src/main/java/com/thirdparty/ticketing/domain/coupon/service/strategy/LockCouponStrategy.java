package com.thirdparty.ticketing.domain.coupon.service.strategy;

import java.util.Optional;

import com.thirdparty.ticketing.domain.coupon.Coupon;

public interface LockCouponStrategy {
    Optional<Coupon> getCouponWithLock(Long couponId);
}
