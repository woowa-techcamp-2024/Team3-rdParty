package com.thirdparty.ticketing.domain.ticket.service.proxy;

import org.hibernate.StaleObjectStateException;
import org.springframework.dao.OptimisticLockingFailureException;

import com.thirdparty.ticketing.domain.common.ErrorCode;
import com.thirdparty.ticketing.domain.common.TicketingException;
import com.thirdparty.ticketing.domain.ticket.dto.SeatSelectionRequest;
import com.thirdparty.ticketing.domain.ticket.dto.TicketPaymentRequest;
import com.thirdparty.ticketing.domain.ticket.service.ReservationTransactionService;

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
            throw new TicketingException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void reservationTicket(String memberEmail, TicketPaymentRequest ticketPaymentRequest) {
        try {
            reservationTransactionService.reservationTicket(memberEmail, ticketPaymentRequest);
        } catch (OptimisticLockingFailureException | StaleObjectStateException e) {
            log.error(e.getMessage(), e);
            throw new TicketingException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
