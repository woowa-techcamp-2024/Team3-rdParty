package com.thirdparty.ticketing.domain.ticket.service;

import org.springframework.stereotype.Service;

import com.thirdparty.ticketing.domain.common.LettuceRepository;
import com.thirdparty.ticketing.domain.member.repository.MemberRepository;
import com.thirdparty.ticketing.domain.payment.PaymentProcessor;
import com.thirdparty.ticketing.domain.seat.repository.SeatRepository;
import com.thirdparty.ticketing.domain.ticket.dto.SeatSelectionRequest;
import com.thirdparty.ticketing.domain.ticket.dto.TicketPaymentRequest;
import com.thirdparty.ticketing.domain.ticket.repository.TicketRepository;

@Service
public class LettuceCacheTicketService extends TicketService {
    private final CacheTicketService cacheTicketService;
    private final LettuceRepository lettuceRepository;

    public LettuceCacheTicketService(
            MemberRepository memberRepository,
            TicketRepository ticketRepository,
            SeatRepository seatRepository,
            PaymentProcessor paymentProcessor,
            CacheTicketService cacheTicketService,
            LettuceRepository lettuceRepository) {
        super(memberRepository, ticketRepository, seatRepository, paymentProcessor);
        this.cacheTicketService = cacheTicketService;
        this.lettuceRepository = lettuceRepository;
    }

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
                cacheTicketService.selectSeat(memberEmail, seatSelectionRequest);
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
                cacheTicketService.reservationTicket(memberEmail, ticketPaymentRequest);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lettuceRepository.unlock(ticketPaymentRequest.getSeatId().toString());
        }
    }
}
