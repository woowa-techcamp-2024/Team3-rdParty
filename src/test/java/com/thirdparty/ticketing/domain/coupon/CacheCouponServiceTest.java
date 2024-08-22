package com.thirdparty.ticketing.domain.coupon;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import com.thirdparty.ticketing.domain.common.TicketingException;
import com.thirdparty.ticketing.domain.coupon.dto.ReceiveCouponRequest;
import com.thirdparty.ticketing.domain.coupon.repository.CouponRepository;
import com.thirdparty.ticketing.domain.coupon.repository.MemberCouponRepository;
import com.thirdparty.ticketing.domain.coupon.service.CouponService;
import com.thirdparty.ticketing.domain.member.Member;
import com.thirdparty.ticketing.domain.member.MemberRole;
import com.thirdparty.ticketing.domain.member.repository.MemberRepository;
import com.thirdparty.ticketing.support.TestContainerStarter;

@SpringBootTest
public class CacheCouponServiceTest extends TestContainerStarter {

    @Autowired private MemberRepository memberRepository;

    @Autowired private CouponRepository couponRepository;

    @Autowired private MemberCouponRepository memberCouponRepository;

    @Autowired
    @Qualifier("lettuceCouponServiceProxy")
    private CouponService lettuceCouponService;

    @Autowired
    @Qualifier("redissonCouponServiceProxy")
    private CouponService redissonCouponService;

    @Autowired
    @Qualifier("optimisticCouponServiceProxy")
    private CouponService optimisticCouponService;

    @Autowired
    @Qualifier("pessimisticCouponServiceProxy")
    private CouponService pessimisticCouponService;

    // 재고가 6개인 쿠폰을 맴버 5명이 2개씩 동시에 발급받는 테스트
    private List<Member> members;
    private Coupon coupon;

    @BeforeEach
    void setUp() {
        members =
                List.of(
                        new Member("member1@example.com", "password", MemberRole.USER),
                        new Member("member2@example.com", "password", MemberRole.USER),
                        new Member("member3@example.com", "password", MemberRole.USER),
                        new Member("member4@example.com", "password", MemberRole.USER),
                        new Member("member5@example.com", "password", MemberRole.USER));
        memberRepository.saveAllAndFlush(members);
        coupon =
                couponRepository.saveAndFlush(
                        Coupon.builder()
                                .name("coupon")
                                .code("coupon-code")
                                .amount(6)
                                .discountRate(10)
                                .build());
    }

    @AfterEach
    void breakUp() {
        memberCouponRepository.deleteAll();
        couponRepository.deleteAll();
        memberRepository.deleteAll();
    }

    // 쿠폰 재고가 6개인 쿠폰을 5명의 맴버가 2개씩 동시에 발급받는 테스트
    @Test
    public void testConcurrentCouponSelectionWithLettuce() throws InterruptedException {
        runConcurrentSeatSelectionTest(lettuceCouponService);
    }

    @Test
    public void testConcurrentCouponSelectionWithRedisson() throws InterruptedException {
        runConcurrentSeatSelectionTest(redissonCouponService);
    }

    private void runConcurrentSeatSelectionTest(CouponService couponServiceProxy)
            throws InterruptedException {

        int threadCount = members.size();
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        AtomicInteger successfulSelections = new AtomicInteger(0);
        AtomicInteger failureSelections = new AtomicInteger(0);

        for (Member member : members) {
            // 각 멤버에 대해 작업을 스레드 풀에 제출
            executorService.submit(
                    () -> {
                        try {
                            // 스레드 풀에서 병렬로 실행되는 작업
                            ReceiveCouponRequest receiveCouponRequest =
                                    new ReceiveCouponRequest(coupon.getCouponId(), 2);
                            couponServiceProxy.receiveCoupon(
                                    member.getEmail(), receiveCouponRequest);
                            successfulSelections.incrementAndGet();
                        } catch (TicketingException e) {
                            failureSelections.incrementAndGet();
                        } catch (Exception e) {
                        } finally {
                            // latch 카운트 감소, 스레드 완료 시 호출
                            latch.countDown();
                        }
                    });
        }

        latch.await();

        Coupon testCoupon = couponRepository.findById(this.coupon.getCouponId()).orElseThrow();
        assertThat(testCoupon).isNotNull();
        assertThat(testCoupon.getAmount()).isEqualTo(0);
        assertThat(successfulSelections.get()).isEqualTo(3);
        assertThat(failureSelections.get()).isEqualTo(2);
    }
}
