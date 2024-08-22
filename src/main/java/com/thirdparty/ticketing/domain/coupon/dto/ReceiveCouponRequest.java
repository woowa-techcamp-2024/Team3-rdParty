package com.thirdparty.ticketing.domain.coupon.dto;

import lombok.Data;

@Data
public class ReceiveCouponRequest {
    private final Long couponId;
    private final Integer amount;
}
