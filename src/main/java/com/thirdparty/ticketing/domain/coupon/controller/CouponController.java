package com.thirdparty.ticketing.domain.coupon.controller;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thirdparty.ticketing.domain.common.LoginMember;
import com.thirdparty.ticketing.domain.coupon.dto.ReceiveCouponRequest;
import com.thirdparty.ticketing.domain.coupon.service.CouponService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/coupon")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponServiceProxy;

    @PostMapping("/receive")
    public ResponseEntity<Void> receiveCoupon(
            @LoginMember String memberEmail,
            @RequestBody @Valid ReceiveCouponRequest receiveCouponRequest) {
        couponServiceProxy.receiveCoupon(memberEmail, receiveCouponRequest);
        return ResponseEntity.ok().build();
    }
}
