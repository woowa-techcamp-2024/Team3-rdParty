package com.thirdparty.ticketing.domain.coupon.service;

import org.springframework.stereotype.Service;

import com.thirdparty.ticketing.domain.coupon.dto.ReceiveCouponRequest;

@Service
public class CouponTransactionalService implements CouponService {

    @Override
    public void receiveCoupon(String userEmail, ReceiveCouponRequest receiveCouponRequest) {}
}
