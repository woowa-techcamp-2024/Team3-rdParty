package com.thirdparty.ticketing.domain.coupon.service.proxy;

import jakarta.persistence.LockTimeoutException;

import org.hibernate.PessimisticLockException;

import com.thirdparty.ticketing.domain.common.ErrorCode;
import com.thirdparty.ticketing.domain.common.TicketingException;
import com.thirdparty.ticketing.domain.coupon.dto.ReceiveCouponRequest;
import com.thirdparty.ticketing.domain.coupon.service.CouponTransactionalService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PessimisticCouponServiceProxy implements CouponServiceProxy {

    private final CouponTransactionalService couponTransactionalService;

    @Override
    public void receiveCoupon(String memberEmail, ReceiveCouponRequest receiveCouponRequest) {
        int retryLimit = 5;
        int sleepDuration = 200;

        while (retryLimit > 0) {
            try {
                couponTransactionalService.receiveCoupon(memberEmail, receiveCouponRequest);
                break;
            } catch (PessimisticLockException | LockTimeoutException e) {
                retryLimit -= 1;
                try {
                    Thread.sleep(sleepDuration);
                } catch (InterruptedException interruptedException) {
                    throw new TicketingException(ErrorCode.INTERNAL_SERVER_ERROR);
                }
            }
        }
    }
}
