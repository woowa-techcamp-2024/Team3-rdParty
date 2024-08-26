package com.thirdparty.ticketing.ticket.service;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.thirdparty.ticketing.exception.ErrorCode;
import com.thirdparty.ticketing.exception.TicketingException;
import com.thirdparty.ticketing.jpa.member.Member;
import com.thirdparty.ticketing.jpa.seat.Seat;
import com.thirdparty.ticketing.jpa.seat.repository.SeatRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ReservationManager {
    private final SeatRepository seatRepository;

    @Transactional
    public void releaseSeat(Member loginMember, long seatId) {
        Seat seat =
                seatRepository
                        .findById(seatId)
                        .orElseThrow(() -> new TicketingException(ErrorCode.NOT_FOUND_SEAT));
        seat.releaseSeat(loginMember);
    }
}
