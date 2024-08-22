package com.thirdparty.ticketing.domain.ticket.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.thirdparty.ticketing.domain.member.Member;
import com.thirdparty.ticketing.domain.ticket.Ticket;

import io.lettuce.core.dynamic.annotation.Param;

public interface TicketRepository extends JpaRepository<Ticket, Integer> {
    @Query(
            """
            SELECT t FROM Ticket t
                        JOIN FETCH t.member m
                        JOIN FETCH t.seat s
                        JOIN FETCH s.seatGrade sg
                        JOIN FETCH s.zone z
                        JOIN FETCH z.performance p
                        WHERE t.member = :member
            """)
    List<Ticket> findAllByMember(@Param("member") Member member);
}
