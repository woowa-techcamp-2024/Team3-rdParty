package com.thirdparty.ticketing.domain.ticket;

import java.util.UUID;

import jakarta.persistence.*;

import com.thirdparty.ticketing.domain.BaseEntity;
import com.thirdparty.ticketing.domain.member.Member;
import com.thirdparty.ticketing.domain.seat.Seat;

import lombok.*;

@Getter
@Entity
@Builder
@AllArgsConstructor
@Table(name = "ticket")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Ticket extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ticketId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id")
    private Seat seat;

    private UUID ticketSerialNumber;
}
