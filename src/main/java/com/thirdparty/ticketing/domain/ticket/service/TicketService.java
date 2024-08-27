package com.thirdparty.ticketing.domain.ticket.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.thirdparty.ticketing.domain.ItemResult;
import com.thirdparty.ticketing.domain.common.ErrorCode;
import com.thirdparty.ticketing.domain.common.TicketingException;
import com.thirdparty.ticketing.domain.member.Member;
import com.thirdparty.ticketing.domain.member.repository.MemberRepository;
import com.thirdparty.ticketing.domain.ticket.dto.response.TicketElement;
import com.thirdparty.ticketing.domain.ticket.repository.TicketRepository;

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
