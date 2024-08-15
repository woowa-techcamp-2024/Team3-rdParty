package com.thirdparty.ticketing.domain.ticket.service;

import org.springframework.stereotype.Service;

import com.thirdparty.ticketing.domain.member.repository.MemberRepository;
import com.thirdparty.ticketing.domain.payment.PaymentProcessor;
import com.thirdparty.ticketing.domain.seat.repository.SeatRepository;
import com.thirdparty.ticketing.domain.ticket.dto.SeatSelectionRequest;
import com.thirdparty.ticketing.domain.ticket.dto.TicketPaymentRequest;
import com.thirdparty.ticketing.domain.ticket.repository.TicketRepository;

@Service
public class RedissonCacheTicketService extends TicketService {

    public RedissonCacheTicketService(
            MemberRepository memberRepository,
            TicketRepository ticketRepository,
            SeatRepository seatRepository,
            PaymentProcessor paymentProcessor) {
        super(memberRepository, ticketRepository, seatRepository, paymentProcessor);
    }

    @Override
    public void selectSeat(String memberEmail, SeatSelectionRequest seatSelectionRequest) {
        // TODO
        //        try {
        //
        //        }catch (){
        //
        //        }
    }

    @Override
    public void reservationTicket(String memberEmail, TicketPaymentRequest ticketPaymentRequest) {}
}
