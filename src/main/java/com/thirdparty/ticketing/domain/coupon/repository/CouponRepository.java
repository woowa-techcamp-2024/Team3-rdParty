package com.thirdparty.ticketing.domain.coupon.repository;

import java.util.Optional;

import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import com.thirdparty.ticketing.domain.coupon.Coupon;

import io.lettuce.core.dynamic.annotation.Param;

public interface CouponRepository extends JpaRepository<Coupon, Long> {

    @Query("SELECT c FROM Coupon as c WHERE c.couponId = :couponId")
    @Lock(LockModeType.NONE)
    Optional<Coupon> findByCouponId(@Param("couponId") Long couponId);

    @Query("SELECT c FROM Coupon as c WHERE c.couponId = :couponId")
    @Lock(LockModeType.OPTIMISTIC)
    Optional<Coupon> findByCouponIdWithOptimistic(@Param("couponId") Long couponId);

    @Query("SELECT c FROM Coupon as c WHERE c.couponId = :couponId")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Coupon> findByCouponIdWithPessimistic(@Param("couponId") Long couponId);
}
