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
        int retryLimit = 10;
        int sleepDuration = 300;

        while (retryLimit > 0) {
            try {
                couponTransactionalService.receiveCoupon(memberEmail, receiveCouponRequest);
                break;
            } catch (PessimisticLockException | LockTimeoutException e) {
                retryLimit -= 1;
                try {
                    Thread.sleep(sleepDuration);
                } catch (InterruptedException interruptedException) {
                    throw new CouponException(ErrorCode.NOT_AVAILABLE_COUPON);
                }
                log.info(
                        "Pessimistic lock failed on thread {}. Retry count: {}",
                        Thread.currentThread().getId(),
                        10 - retryLimit);
            }
        }
        log.info("Pessimistic lock success on thread {}", Thread.currentThread().getId());
    }
}
