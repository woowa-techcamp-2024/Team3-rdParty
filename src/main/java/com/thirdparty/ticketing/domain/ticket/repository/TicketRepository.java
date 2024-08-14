package com.thirdparty.ticketing.domain.ticket.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.thirdparty.ticketing.domain.member.Member;
import com.thirdparty.ticketing.domain.ticket.Ticket;

public interface TicketRepository extends JpaRepository<Ticket, Integer> {
    List<Ticket> findAllByMember(Member member);
}
