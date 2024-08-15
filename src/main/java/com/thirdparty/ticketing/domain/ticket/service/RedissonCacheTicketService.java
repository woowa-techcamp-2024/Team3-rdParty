package com.thirdparty.ticketing.domain.ticket.service;

import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;


import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thirdparty.ticketing.domain.member.repository.MemberRepository;
import com.thirdparty.ticketing.domain.payment.PaymentProcessor;
import com.thirdparty.ticketing.domain.seat.Seat;
import com.thirdparty.ticketing.domain.seat.repository.SeatRepository;
import com.thirdparty.ticketing.domain.ticket.dto.SeatSelectionRequest;
import com.thirdparty.ticketing.domain.ticket.dto.TicketPaymentRequest;
import com.thirdparty.ticketing.domain.ticket.repository.TicketRepository;

@Service
public class RedissonCacheTicketService extends TicketService {

    private final CacheTicketService cacheTicketService;
    private final RedissonClient redissonClient;

    public RedissonCacheTicketService(
            MemberRepository memberRepository,
            TicketRepository ticketRepository,
            SeatRepository seatRepository,
            CacheTicketService cacheTicketService,
            RedissonClient redissonClient,
            PaymentProcessor paymentProcessor) {
        super(memberRepository, ticketRepository, seatRepository, paymentProcessor);
        this.cacheTicketService = cacheTicketService;
        this.redissonClient = redissonClient;
    }

    @Override
    public void selectSeat(String memberEmail, SeatSelectionRequest seatSelectionRequest) {
        RLock lock = redissonClient.getLock(seatSelectionRequest.getSeatId().toString());

        try {
            boolean available = lock.tryLock(5, 300, TimeUnit.MICROSECONDS);
            if (!available) {
                return;
            }

            cacheTicketService.selectSeat(memberEmail, seatSelectionRequest);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
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

            cacheTicketService.reservationTicket(memberEmail, ticketPaymentRequest);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }
}
