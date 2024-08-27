package com.thirdparty.ticketing.domain.ticket.service.proxy;

import com.thirdparty.ticketing.domain.ticket.dto.request.SeatSelectionRequest;
import com.thirdparty.ticketing.domain.ticket.dto.request.TicketPaymentRequest;
import com.thirdparty.ticketing.domain.ticket.service.ReservationService;
import com.thirdparty.ticketing.global.lock.lettuce.LettuceLockAnnotation;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LettuceReservationServiceProxy implements ReservationServiceProxy {
    private final ReservationService reservationService;

    @Override
    @LettuceLockAnnotation(key = "#seatSelectionRequest.seatId")
    public void selectSeat(String memberEmail, SeatSelectionRequest seatSelectionRequest) {
        reservationService.selectSeat(memberEmail, seatSelectionRequest);
    }

    @Override
    @LettuceLockAnnotation(key = "#ticketPaymentRequest.seatId")
    public void reservationTicket(String memberEmail, TicketPaymentRequest ticketPaymentRequest) {
        reservationService.reservationTicket(memberEmail, ticketPaymentRequest);
    }

    @Override
    public void releaseSeat(String memberEmail, SeatSelectionRequest seatSelectionRequest) {
        reservationService.releaseSeat(memberEmail, seatSelectionRequest);
    }
}
