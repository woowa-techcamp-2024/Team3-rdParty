package com.thirdparty.ticketing.domain.ticket.service;

import java.util.concurrent.TimeUnit;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.transaction.annotation.Transactional;

import com.thirdparty.ticketing.domain.common.ErrorCode;
import com.thirdparty.ticketing.domain.common.TicketingException;
import com.thirdparty.ticketing.domain.ticket.dto.SeatSelectionRequest;
import com.thirdparty.ticketing.domain.ticket.dto.TicketPaymentRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class ReddisonReservationServiceProxy implements ReservationServiceProxy {
    private final RedissonClient redissonClient;
    private final ReservationTransactionService reservationTransactionService;

    @Override
    public void selectSeat(String memberEmail, SeatSelectionRequest seatSelectionRequest) {
        RLock lock = redissonClient.getLock(seatSelectionRequest.getSeatId().toString());

        try {
            if (!lock.tryLock(1, 60, TimeUnit.SECONDS)) {
                return;
            }
            reservationTransactionService.selectSeat(memberEmail, seatSelectionRequest);
        } catch (InterruptedException e) {
            throw new TicketingException(ErrorCode.NOT_SELECTABLE_SEAT);
        } finally {
            try {
                lock.unlock();
            } catch (IllegalMonitorStateException e) {
                log.info("Redisson Lock Already UnLock");
            }
            log.info("finished lock on {}", seatSelectionRequest.getSeatId().toString());
        }
    }

    @Override
    @Transactional
    public void reservationTicket(String memberEmail, TicketPaymentRequest ticketPaymentRequest) {
        RLock lock = redissonClient.getLock(ticketPaymentRequest.getSeatId().toString());

        try {
            boolean available = lock.tryLock(5, 300, TimeUnit.MICROSECONDS);
            if (!available) {
                return;
            }

            reservationTransactionService.reservationTicket(memberEmail, ticketPaymentRequest);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }
}
