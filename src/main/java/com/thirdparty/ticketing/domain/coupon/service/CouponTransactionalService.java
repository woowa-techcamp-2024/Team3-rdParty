package com.thirdparty.ticketing.domain.coupon.service;

import jakarta.transaction.Transactional;

import com.thirdparty.ticketing.domain.common.CouponException;
import com.thirdparty.ticketing.domain.common.ErrorCode;
import com.thirdparty.ticketing.domain.coupon.Coupon;
import com.thirdparty.ticketing.domain.coupon.CouponMember;
import com.thirdparty.ticketing.domain.coupon.dto.ReceiveCouponRequest;
import com.thirdparty.ticketing.domain.coupon.repository.CouponMemberRepository;
import com.thirdparty.ticketing.domain.coupon.service.strategy.LockCouponStrategy;
import com.thirdparty.ticketing.domain.member.Member;
import com.thirdparty.ticketing.domain.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class CouponTransactionalService implements CouponService {

    private final MemberRepository memberRepository;
    private final LockCouponStrategy lockCouponStrategy;
    private final CouponMemberRepository couponMemberRepository;

    @Override
    @Transactional
    public void receiveCoupon(String memberEmail, ReceiveCouponRequest receiveCouponRequest) {
        Long couponId = receiveCouponRequest.getCouponId();
        Integer amount = receiveCouponRequest.getAmount();

        Member member =
                memberRepository
                        .findByEmail(memberEmail)
                        .orElseThrow(() -> new CouponException(ErrorCode.NOT_FOUND_MEMBER));
        log.info("Receive coupon for member {}", memberEmail);
        Coupon coupon =
                lockCouponStrategy
                        .getCouponWithLock(couponId)
                        .orElseThrow(() -> new CouponException(ErrorCode.NOT_FOUND_COUPON));

        couponMemberRepository.save(CouponMember.CreateCouponMember(coupon, member, amount));
    }
}
