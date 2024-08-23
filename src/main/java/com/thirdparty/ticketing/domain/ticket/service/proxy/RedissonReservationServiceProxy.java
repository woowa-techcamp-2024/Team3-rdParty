package com.thirdparty.ticketing.domain.ticket.service.proxy;

import java.util.concurrent.TimeUnit;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import com.thirdparty.ticketing.domain.common.ErrorCode;
import com.thirdparty.ticketing.domain.common.TicketingException;
import com.thirdparty.ticketing.domain.ticket.dto.request.SeatSelectionRequest;
import com.thirdparty.ticketing.domain.ticket.dto.request.TicketPaymentRequest;
import com.thirdparty.ticketing.domain.ticket.service.ReservationTransactionService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class RedissonReservationServiceProxy implements ReservationServiceProxy {

    private final RedissonClient redissonClient;
    private final ReservationTransactionService reservationTransactionService;

    private void performSeatAction(String seatId, Runnable action) {
        String lockPrefix = "seat:";
        RLock lock = redissonClient.getLock(lockPrefix + seatId);

        int tryTime = 1;
        int releaseTime = 60;

        try {
            if (!lock.tryLock(tryTime, releaseTime, TimeUnit.SECONDS)) {
                return;
            }
            action.run();
        } catch (InterruptedException e) {
            throw new TicketingException(ErrorCode.NOT_SELECTABLE_SEAT, e);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void selectSeat(String memberEmail, SeatSelectionRequest seatSelectionRequest) {
        performSeatAction(
                seatSelectionRequest.getSeatId().toString(),
                () -> reservationTransactionService.selectSeat(memberEmail, seatSelectionRequest));
    }

    @Override
    public void reservationTicket(String memberEmail, TicketPaymentRequest ticketPaymentRequest) {
        performSeatAction(
                ticketPaymentRequest.getSeatId().toString(),
                () ->
                        reservationTransactionService.reservationTicket(
                                memberEmail, ticketPaymentRequest));
    }

    @Override
    public void releaseSeat(String memberEmail, SeatSelectionRequest seatSelectionRequest) {
        reservationTransactionService.releaseSeat(memberEmail, seatSelectionRequest);
    }
}
