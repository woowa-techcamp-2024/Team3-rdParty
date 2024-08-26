package com.thirdparty.ticketing.ticket.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.thirdparty.common.ItemResult;
import com.thirdparty.ticketing.exception.ErrorCode;
import com.thirdparty.ticketing.exception.TicketingException;
import com.thirdparty.ticketing.jpa.member.Member;
import com.thirdparty.ticketing.jpa.member.repository.MemberRepository;
import com.thirdparty.ticketing.jpa.ticket.repository.TicketRepository;
import com.thirdparty.ticketing.ticket.dto.response.TicketElement;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TicketService {
    protected final MemberRepository memberRepository;
    protected final TicketRepository ticketRepository;

    public ItemResult<TicketElement> selectMyTicket(String memberEmail) {
        Member member =
                memberRepository
                        .findByEmail(memberEmail)
                        .orElseThrow(() -> new TicketingException(ErrorCode.NOT_FOUND_MEMBER));

        List<TicketElement> tickets =
                ticketRepository.findAllByMember(member).stream().map(TicketElement::of).toList();

        return ItemResult.of(tickets);
    }
}
