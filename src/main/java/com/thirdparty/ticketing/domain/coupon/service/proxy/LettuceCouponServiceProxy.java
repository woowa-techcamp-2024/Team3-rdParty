package com.thirdparty.ticketing.domain.coupon.service.proxy;

import com.thirdparty.ticketing.domain.common.CouponException;
import com.thirdparty.ticketing.domain.common.ErrorCode;
import com.thirdparty.ticketing.domain.common.LettuceRepository;
import com.thirdparty.ticketing.domain.coupon.dto.ReceiveCouponRequest;
import com.thirdparty.ticketing.domain.coupon.service.CouponTransactionalService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class LettuceCouponServiceProxy implements CouponServiceProxy {

    private final LettuceRepository lettuceRepository;
    private final CouponTransactionalService couponTransactionalService;

    @Override
    public void receiveCoupon(String memberEmail, ReceiveCouponRequest receiveCouponRequest) {
        int retryLimit = 10;
        int sleepDuration = 300;

        String couponId = receiveCouponRequest.getCouponId().toString();
        String lockKey = "coupon:" + couponId;
        try {
            while (retryLimit > 0 && !lettuceRepository.couponLock(lockKey)) {
                retryLimit -= 1;
                Thread.sleep(sleepDuration);
            }

            if (retryLimit > 0) {
                couponTransactionalService.receiveCoupon(memberEmail, receiveCouponRequest);
            }

        } catch (InterruptedException e) {
            log.error("Failed to lock coupon: {}", couponId, e);
            throw new CouponException(ErrorCode.NOT_SELECTABLE_SEAT, e);
        } finally {
            lettuceRepository.unlock(lockKey);
        }
    }
}
