package com.thirdparty.ticketing.domain.coupon.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.thirdparty.ticketing.domain.coupon.CouponMember;

public interface MemberCouponRepository extends JpaRepository<CouponMember, Long> {}
