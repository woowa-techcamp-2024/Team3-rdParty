package com.thirdparty.ticketing.domain.ticket.service;

import com.thirdparty.ticketing.domain.common.LettuceRepository;
import com.thirdparty.ticketing.domain.ticket.dto.SeatSelectionRequest;
import com.thirdparty.ticketing.domain.ticket.dto.TicketPaymentRequest;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LettuceReservationServiceProxy implements ReservationServiceProxy {
    private final LettuceRepository lettuceRepository;
    private final ReservationTransactionService reservationTransactionService;

    @Override
    public void selectSeat(String memberEmail, SeatSelectionRequest seatSelectionRequest) {
        int limit = 5;
        try {
            while (limit > 0
                    && !lettuceRepository.seatLock(seatSelectionRequest.getSeatId().toString())) {
                limit -= 1;
                Thread.sleep(300);
            }

            if (limit > 0) {
                reservationTransactionService.selectSeat(memberEmail, seatSelectionRequest);
            }

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lettuceRepository.unlock(seatSelectionRequest.getSeatId().toString());
        }
    }

    @Override
    public void reservationTicket(String memberEmail, TicketPaymentRequest ticketPaymentRequest) {
        int limit = 5;
        try {
            while (limit > 0
                    && !lettuceRepository.seatLock(ticketPaymentRequest.getSeatId().toString())) {
                limit -= 1;
                Thread.sleep(300);
            }

            if (limit > 0) {
                reservationTransactionService.reservationTicket(memberEmail, ticketPaymentRequest);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lettuceRepository.unlock(ticketPaymentRequest.getSeatId().toString());
        }
    }
}
