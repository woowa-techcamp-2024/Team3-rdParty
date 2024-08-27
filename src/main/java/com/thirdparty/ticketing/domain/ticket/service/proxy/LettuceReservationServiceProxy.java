package com.thirdparty.ticketing.domain.ticket.service.proxy;

import com.thirdparty.ticketing.domain.common.ErrorCode;
import com.thirdparty.ticketing.domain.common.LettuceRepository;
import com.thirdparty.ticketing.domain.common.TicketingException;
import com.thirdparty.ticketing.domain.ticket.dto.request.SeatSelectionRequest;
import com.thirdparty.ticketing.domain.ticket.dto.request.TicketPaymentRequest;
import com.thirdparty.ticketing.domain.ticket.service.ReservationService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LettuceReservationServiceProxy implements ReservationServiceProxy {

    private final LettuceRepository lettuceRepository;
    private final ReservationService reservationService;

    private void performSeatAction(String seatId, Runnable action) {
        int retryLimit = 5;
        int sleepDuration = 300;
        String lockPrefix = "seat:";
        String lockKey = lockPrefix + seatId;
        try {
            while (retryLimit > 0 && !lettuceRepository.seatLock(lockKey)) {
                retryLimit -= 1;
                Thread.sleep(sleepDuration);
            }

            if (retryLimit > 0) {
                action.run();
            } else {
                throw new TicketingException(ErrorCode.NOT_SELECTABLE_SEAT);
            }

        } catch (InterruptedException e) {
            throw new TicketingException(ErrorCode.NOT_SELECTABLE_SEAT, e);
        } finally {
            lettuceRepository.unlock(lockKey);
        }
    }

    @Override
    public void selectSeat(String memberEmail, SeatSelectionRequest seatSelectionRequest) {
        performSeatAction(
                seatSelectionRequest.getSeatId().toString(),
                () -> reservationService.selectSeat(memberEmail, seatSelectionRequest));
    }

    @Override
    public void reservationTicket(String memberEmail, TicketPaymentRequest ticketPaymentRequest) {
        performSeatAction(
                ticketPaymentRequest.getSeatId().toString(),
                () -> reservationService.reservationTicket(memberEmail, ticketPaymentRequest));
    }

    @Override
    public void releaseSeat(String memberEmail, SeatSelectionRequest seatSelectionRequest) {
        reservationService.releaseSeat(memberEmail, seatSelectionRequest);
    }
}
