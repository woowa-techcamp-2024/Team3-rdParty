package com.thirdparty.ticketing.domain.ticket.service;

import org.springframework.stereotype.Service;

import com.thirdparty.ticketing.domain.member.repository.MemberRepository;
import com.thirdparty.ticketing.domain.seat.repository.SeatRepository;
import com.thirdparty.ticketing.domain.ticket.dto.SeatSelectionRequest;
import com.thirdparty.ticketing.domain.ticket.dto.TicketPaymentRequest;
import com.thirdparty.ticketing.domain.ticket.repository.TicketRepository;

@Service
public class PersistenceTicketService extends TicketService {
    public PersistenceTicketService(
            MemberRepository memberRepository,
            TicketRepository ticketRepository,
            SeatRepository seatRepository) {
        super(memberRepository, ticketRepository, seatRepository);
    }

    @Override
    public void selectSeat(SeatSelectionRequest seatSelectionRequest) {}

    @Override
    public void reservationTicket(TicketPaymentRequest ticketPaymentRequest) {}
}
