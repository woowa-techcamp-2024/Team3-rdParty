package com.thirdparty.ticketing.domain.ticket.service;

import org.springframework.stereotype.Service;

import com.thirdparty.ticketing.domain.member.repository.MemberRepository;
import com.thirdparty.ticketing.domain.payment.PaymentProcessor;
import com.thirdparty.ticketing.domain.seat.repository.SeatRepository;
import com.thirdparty.ticketing.domain.ticket.dto.SeatSelectionRequest;
import com.thirdparty.ticketing.domain.ticket.dto.TicketPaymentRequest;
import com.thirdparty.ticketing.domain.ticket.repository.TicketRepository;

@Service
public class LettuceCacheTicketService extends TicketService {
    private final CacheTicketService cacheTicketService;

    public LettuceCacheTicketService(
            MemberRepository memberRepository,
            TicketRepository ticketRepository,
            SeatRepository seatRepository,
            PaymentProcessor paymentProcessor,
            CacheTicketService cacheTicketService) {
        super(memberRepository, ticketRepository, seatRepository, paymentProcessor);
        this.cacheTicketService = cacheTicketService;
    }

    @Override
    public void selectSeat(String memberEmail, SeatSelectionRequest seatSelectionRequest) {
        // TODO spin lock으로 일정 횟수 만큼 lock을 얻어오기
    }

    @Override
    public void reservationTicket(String memberEmail, TicketPaymentRequest ticketPaymentRequest) {}
}
