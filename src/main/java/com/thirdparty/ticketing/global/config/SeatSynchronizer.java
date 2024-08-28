package com.thirdparty.ticketing.global.config;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.thirdparty.ticketing.domain.common.ErrorCode;
import com.thirdparty.ticketing.domain.common.TicketingException;
import com.thirdparty.ticketing.domain.member.Member;
import com.thirdparty.ticketing.domain.member.repository.MemberRepository;
import com.thirdparty.ticketing.domain.seat.RedisSeat;
import com.thirdparty.ticketing.domain.seat.Seat;
import com.thirdparty.ticketing.domain.seat.repository.LettuceSeatRepository;
import com.thirdparty.ticketing.domain.seat.repository.SeatRepository;
import com.thirdparty.ticketing.global.lock.redisson.RedissonLockAnnotation;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SeatSynchronizer {

    private final LettuceSeatRepository lettuceSeatRepository;
    private final SeatRepository seatRepository;
    private final MemberRepository memberRepository;

    @RedissonLockAnnotation(key = "#seatId")
    @Transactional
    public void occupy(Long seatId) {
        RedisSeat redisSeat =
                lettuceSeatRepository
                        .findBySeatId(seatId)
                        .orElseThrow(() -> new TicketingException(ErrorCode.NOT_FOUND_SEAT));

        Seat seat =
                seatRepository
                        .findById(seatId)
                        .orElseThrow(() -> new TicketingException(ErrorCode.NOT_FOUND_SEAT));

        Member member =
                memberRepository
                        .findById(redisSeat.getMemberId())
                        .orElseThrow(() -> new TicketingException(ErrorCode.NOT_FOUND_MEMBER));

        seat.assignByMember(member);
    }

    @RedissonLockAnnotation(key = "#seatId")
    @Transactional
    public void release(Long seatId) {
        RedisSeat redisSeat =
                lettuceSeatRepository
                        .findBySeatId(seatId)
                        .orElseThrow(() -> new TicketingException(ErrorCode.NOT_FOUND_SEAT));

        Seat seat =
                seatRepository
                        .findById(seatId)
                        .orElseThrow(() -> new TicketingException(ErrorCode.NOT_FOUND_SEAT));

        Member member =
                memberRepository
                        .findById(redisSeat.getMemberId())
                        .orElseThrow(() -> new TicketingException(ErrorCode.NOT_FOUND_MEMBER));

        seat.releaseSeat(member);
    }
}
