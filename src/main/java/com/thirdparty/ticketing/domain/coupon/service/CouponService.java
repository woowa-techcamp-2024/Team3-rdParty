package com.thirdparty.ticketing.domain.coupon.service;

import com.thirdparty.ticketing.domain.coupon.dto.ReceiveCouponRequest;

public interface CouponService {
    void receiveCoupon(String userEmail, ReceiveCouponRequest receiveCouponRequest);
}
