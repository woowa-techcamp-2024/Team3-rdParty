package com.thirdparty.ticketing.domain.ticket.service;

import com.thirdparty.ticketing.domain.member.repository.MemberRepository;
import com.thirdparty.ticketing.domain.payment.PaymentProcessor;
import com.thirdparty.ticketing.domain.seat.repository.SeatRepository;
import com.thirdparty.ticketing.domain.ticket.dto.SeatSelectionRequest;
import com.thirdparty.ticketing.domain.ticket.dto.TicketPaymentRequest;
import com.thirdparty.ticketing.domain.ticket.repository.TicketRepository;

public class CacheTicketService extends TicketService {
    public CacheTicketService(
            MemberRepository memberRepository,
            TicketRepository ticketRepository,
            SeatRepository seatRepository,
            PaymentProcessor paymentProcessor) {
        super(memberRepository, ticketRepository, seatRepository, paymentProcessor);
    }

    @Override
    public void selectSeat(SeatSelectionRequest seatSelectionRequest) {}

    @Override
    public void reservationTicket(TicketPaymentRequest ticketPaymentRequest) {}
}
