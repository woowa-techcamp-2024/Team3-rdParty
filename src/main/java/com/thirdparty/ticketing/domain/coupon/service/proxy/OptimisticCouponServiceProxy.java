package com.thirdparty.ticketing.domain.coupon.service.proxy;

import org.hibernate.StaleObjectStateException;
import org.springframework.dao.OptimisticLockingFailureException;

import com.thirdparty.ticketing.domain.common.CouponException;
import com.thirdparty.ticketing.domain.common.ErrorCode;
import com.thirdparty.ticketing.domain.coupon.dto.ReceiveCouponRequest;
import com.thirdparty.ticketing.domain.coupon.service.CouponTransactionalService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class OptimisticCouponServiceProxy implements CouponServiceProxy {

    private final CouponTransactionalService couponTransactionalService;

    @Override
    public void receiveCoupon(String memberEmail, ReceiveCouponRequest receiveCouponRequest) {
        int retryLimit = 10;
        int sleepDuration = 300;
        while (retryLimit > 0) {
            try {
                couponTransactionalService.receiveCoupon(memberEmail, receiveCouponRequest);
                break;
            } catch (OptimisticLockingFailureException | StaleObjectStateException e) {
                retryLimit -= 1;
                try {
                    Thread.sleep(sleepDuration);
                } catch (InterruptedException e1) {
                    throw new CouponException(ErrorCode.NOT_SELECTABLE_SEAT, e1);
                }
                log.info(
                        "Optimistic lock failed on thread {}. Retry count: {}",
                        Thread.currentThread().getId(),
                        10 - retryLimit);
            }
        }
        log.info("Optimistic lock success on thread {}", Thread.currentThread().getId());
    }
}
