package com.thirdparty.ticketing.ticket.service.proxy;

import org.hibernate.StaleObjectStateException;
import org.springframework.dao.OptimisticLockingFailureException;

import com.thirdparty.ticketing.exception.ErrorCode;
import com.thirdparty.ticketing.exception.TicketingException;
import com.thirdparty.ticketing.ticket.dto.request.SeatSelectionRequest;
import com.thirdparty.ticketing.ticket.dto.request.TicketPaymentRequest;
import com.thirdparty.ticketing.ticket.service.ReservationTransactionService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class OptimisticReservationServiceProxy implements ReservationServiceProxy {
    private final ReservationTransactionService reservationTransactionService;

    @Override
    public void selectSeat(String memberEmail, SeatSelectionRequest seatSelectionRequest) {
        try {
            reservationTransactionService.selectSeat(memberEmail, seatSelectionRequest);
        } catch (OptimisticLockingFailureException | StaleObjectStateException e) {
            log.error(e.getMessage(), e);
            throw new TicketingException(ErrorCode.NOT_SELECTABLE_SEAT);
        }
    }

    @Override
    public void reservationTicket(String memberEmail, TicketPaymentRequest ticketPaymentRequest) {
        try {
            reservationTransactionService.reservationTicket(memberEmail, ticketPaymentRequest);
        } catch (OptimisticLockingFailureException | StaleObjectStateException e) {
            log.error(e.getMessage(), e);
            throw new TicketingException(ErrorCode.NOT_SELECTABLE_SEAT);
        }
    }

    @Override
    public void releaseSeat(String memberEmail, SeatSelectionRequest seatSelectionRequest) {
        reservationTransactionService.releaseSeat(memberEmail, seatSelectionRequest);
    }
}
