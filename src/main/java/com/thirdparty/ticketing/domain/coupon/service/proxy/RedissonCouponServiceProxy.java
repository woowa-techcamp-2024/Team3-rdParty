package com.thirdparty.ticketing.domain.coupon.service.proxy;

import java.util.concurrent.TimeUnit;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import com.thirdparty.ticketing.domain.common.CouponException;
import com.thirdparty.ticketing.domain.common.ErrorCode;
import com.thirdparty.ticketing.domain.coupon.dto.ReceiveCouponRequest;
import com.thirdparty.ticketing.domain.coupon.service.CouponTransactionalService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RedissonCouponServiceProxy implements CouponServiceProxy {

    private final RedissonClient redissonClient;
    private final CouponTransactionalService couponTransactionalService;

    @Override
    public void receiveCoupon(String memberEmail, ReceiveCouponRequest receiveCouponRequest) {
        int tryTime = 3;
        int releaseTime = 60;
        Long couponId = receiveCouponRequest.getCouponId();
        String lockKey = "coupon:" + couponId;
        RLock lock = redissonClient.getLock(lockKey);

        try {
            if (!lock.tryLock(tryTime, releaseTime, TimeUnit.SECONDS)) {
                return;
            }
            couponTransactionalService.receiveCoupon(memberEmail, receiveCouponRequest);
        } catch (InterruptedException e) {
            throw new CouponException(ErrorCode.NOT_AVAILABLE_COUPON, e);
        } finally {
            lock.unlock();
        }
    }
}
