package com.thirdparty.ticketing.domain.coupon;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import com.thirdparty.ticketing.domain.BaseEntity;
import com.thirdparty.ticketing.domain.member.Member;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Builder
@AllArgsConstructor
@Table(name = "coupon")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CouponMember extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long couponMemberId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id")
    private Coupon coupon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private Integer amount;

    public CouponMember(Coupon coupon, Member member, Integer amount) {
        this.coupon = coupon;
        this.member = member;
        this.amount = amount;
    }

    public static CouponMember CreateCouponMember(Coupon coupon, Member member, Integer amount) {
        coupon.giveCoupon(amount);

        return CouponMember.builder().coupon(coupon).member(member).amount(amount).build();
    }
}
