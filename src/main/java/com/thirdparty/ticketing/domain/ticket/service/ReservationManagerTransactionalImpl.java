package com.thirdparty.ticketing.domain.ticket.service;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.thirdparty.ticketing.domain.common.ErrorCode;
import com.thirdparty.ticketing.domain.common.TicketingException;
import com.thirdparty.ticketing.domain.member.Member;
import com.thirdparty.ticketing.domain.seat.Seat;
import com.thirdparty.ticketing.domain.seat.repository.SeatRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ReservationManagerTransactionalImpl implements ReservationManager {
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
