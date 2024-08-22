package com.thirdparty.ticketing.domain.coupon.service;

import com.thirdparty.ticketing.domain.coupon.dto.ReceiveCouponRequest;
import com.thirdparty.ticketing.domain.coupon.service.strategy.LockCouponStrategy;
import com.thirdparty.ticketing.domain.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CouponTransactionalService implements CouponService {

    private final MemberRepository memberRepository;
    private final LockCouponStrategy lockCouponStrategy;

    @Override
    public void receiveCoupon(String userEmail, ReceiveCouponRequest receiveCouponRequest) {}
}
