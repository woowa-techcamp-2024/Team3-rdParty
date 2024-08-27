package com.thirdparty.ticketing.domain.ticket.service.proxy;

import com.thirdparty.ticketing.domain.ticket.dto.request.SeatSelectionRequest;
import com.thirdparty.ticketing.domain.ticket.dto.request.TicketPaymentRequest;
import com.thirdparty.ticketing.domain.ticket.service.ReservationService;
import com.thirdparty.ticketing.global.lock.redisson.RedissonLockAnnotation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class RedissonReservationServiceProxy implements ReservationServiceProxy {

    private final ReservationService reservationService;

    @Override
    @RedissonLockAnnotation(key = "#seatSelectionRequest.seatId")
    public void selectSeat(String memberEmail, SeatSelectionRequest seatSelectionRequest) {
        reservationService.selectSeat(memberEmail, seatSelectionRequest);
    }

    @Override
    @RedissonLockAnnotation(key = "#ticketPaymentRequest.seatId")
    public void reservationTicket(String memberEmail, TicketPaymentRequest ticketPaymentRequest) {
        reservationService.reservationTicket(memberEmail, ticketPaymentRequest);
    }

    @Override
    public void releaseSeat(String memberEmail, SeatSelectionRequest seatSelectionRequest) {
        reservationService.releaseSeat(memberEmail, seatSelectionRequest);
    }
}
