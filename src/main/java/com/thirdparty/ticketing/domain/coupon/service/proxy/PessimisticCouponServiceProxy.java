package com.thirdparty.ticketing.domain.coupon.service.proxy;

import jakarta.persistence.LockTimeoutException;

import org.hibernate.PessimisticLockException;

import com.thirdparty.ticketing.domain.common.CouponException;
import com.thirdparty.ticketing.domain.common.ErrorCode;
import com.thirdparty.ticketing.domain.coupon.dto.ReceiveCouponRequest;
import com.thirdparty.ticketing.domain.coupon.service.CouponTransactionalService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class PessimisticCouponServiceProxy implements CouponServiceProxy {

    private final CouponTransactionalService couponTransactionalService;

    @Override
    public void receiveCoupon(String memberEmail, ReceiveCouponRequest receiveCouponRequest) {
        int sleepDuration = 300;
        try {
            log.info("Pessimistic lock on thread {}", Thread.currentThread().getId());
            couponTransactionalService.receiveCoupon(memberEmail, receiveCouponRequest);
        } catch (PessimisticLockException | LockTimeoutException e) {
            throw new CouponException(ErrorCode.NOT_AVAILABLE_COUPON);
        }
        log.info("Pessimistic lock success on thread {}", Thread.currentThread().getId());
    }
}
