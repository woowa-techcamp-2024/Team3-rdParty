package com.thirdparty.ticketing.domain.ticket.service;

import java.util.List;

import com.thirdparty.ticketing.domain.ItemResult;
import com.thirdparty.ticketing.domain.common.TicketingException;
import com.thirdparty.ticketing.domain.member.Member;
import com.thirdparty.ticketing.domain.member.repository.MemberRepository;
import com.thirdparty.ticketing.domain.seat.repository.SeatRepository;
import com.thirdparty.ticketing.domain.ticket.dto.SeatSelectionRequest;
import com.thirdparty.ticketing.domain.ticket.dto.TicketElement;
import com.thirdparty.ticketing.domain.ticket.dto.TicketPaymentRequest;
import com.thirdparty.ticketing.domain.ticket.repository.TicketRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class TicketService {
    private final MemberRepository memberRepository;
    private final TicketRepository ticketRepository;
    private final SeatRepository seatRepository;

    public ItemResult<TicketElement> selectMyTicket(String memberEmail) {
        Member member =
                memberRepository
                        .findByEmail(memberEmail)
                        .orElseThrow(() -> new TicketingException("Member not found"));

        List<TicketElement> tickets =
                ticketRepository.findAllByMember(member).stream().map(TicketElement::of).toList();

        return ItemResult.of(tickets);
    }

    public abstract void selectSeat(SeatSelectionRequest seatSelectionRequest);

    public abstract void reservationTicket(TicketPaymentRequest ticketPaymentRequest);
}
