package com.thirdparty.ticketing.domain.ticket.service;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.thirdparty.ticketing.domain.common.ErrorCode;
import com.thirdparty.ticketing.domain.common.TicketingException;
import com.thirdparty.ticketing.domain.member.Member;
import com.thirdparty.ticketing.domain.member.repository.MemberRepository;
import com.thirdparty.ticketing.domain.seat.Seat;
import com.thirdparty.ticketing.domain.seat.repository.SeatRepository;
import com.thirdparty.ticketing.domain.ticket.dto.request.SeatSelectionRequest;
import com.thirdparty.ticketing.domain.ticket.dto.request.TicketPaymentRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@RequiredArgsConstructor
public class NewRedisReservationService implements ReservationService{

    private final MemberRepository memberRepository;
    private final SeatRepository seatRepository;
    private final StringRedisTemplate redisTemplate;
    private final int reservationReleaseDelay;

    private final static String SEAT_CONST = "seat-selected-number:";

    @Override
    public void selectSeat(String memberEmail, SeatSelectionRequest seatSelectionRequest) {
        Seat seat =
                seatRepository.findById(seatSelectionRequest.getSeatId())
                        .orElseThrow(() -> new TicketingException(ErrorCode.NOT_FOUND_SEAT));

        Member member =
                memberRepository.findByEmail(memberEmail)
                        .orElseThrow(() -> new TicketingException(ErrorCode.NOT_FOUND_MEMBER));

        Boolean result = redisTemplate.opsForValue()
                .setIfAbsent(SEAT_CONST + seat.getSeatId(), member.getEmail(), reservationReleaseDelay, TimeUnit.SECONDS);

        if (Boolean.FALSE.equals(result)) {
            throw new TicketingException(ErrorCode.NOT_SELECTABLE_SEAT);
        }
    }

    @Override
    public void reservationTicket(String memberEmail, TicketPaymentRequest ticketPaymentRequest) {
        // do nothing
    }

    @Override
    public void releaseSeat(String memberEmail, SeatSelectionRequest seatSelectionRequest) {
        redisTemplate.delete(SEAT_CONST + seatSelectionRequest.getSeatId());
    }
}
