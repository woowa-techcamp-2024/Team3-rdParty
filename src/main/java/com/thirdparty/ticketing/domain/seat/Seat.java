package com.thirdparty.ticketing.domain.seat;

import jakarta.persistence.*;

import com.thirdparty.ticketing.domain.BaseEntity;
import com.thirdparty.ticketing.domain.common.ErrorCode;
import com.thirdparty.ticketing.domain.common.TicketingException;
import com.thirdparty.ticketing.domain.member.Member;
import com.thirdparty.ticketing.domain.zone.Zone;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Entity
@Table(name = "seat")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Slf4j
public class Seat extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seatId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "zone_id", nullable = false)
    private Zone zone;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_grade_id", nullable = false)
    private SeatGrade seatGrade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(length = 32, nullable = false)
    private String seatCode;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(length = 16, nullable = false)
    private SeatStatus seatStatus = SeatStatus.SELECTABLE;

    @Version private Long version;

    public Seat(String seatCode, SeatStatus seatStatus) {
        this.seatCode = seatCode;
        this.seatStatus = seatStatus;
    }

    public boolean isSelectable() {
        return seatStatus.isSelectable();
    }

    public void assignByMember(Member member) {
        if (!isSelectable()) {
            throw new TicketingException(ErrorCode.NOT_SELECTABLE_SEAT);
        }
        log.info("seat occupied by {}", member.getEmail());
        this.member = member;
        this.seatStatus = SeatStatus.SELECTED;
    }

    public void markAsPendingPayment() {
        if (!seatStatus.isSelected()) {
            throw new TicketingException(ErrorCode.INVALID_SEAT_STATUS);
        }
        this.seatStatus = SeatStatus.PENDING_PAYMENT;
    }

    public void markAsPaid() {
        if (!seatStatus.isPendingPayment()) {
            throw new TicketingException(ErrorCode.INVALID_SEAT_STATUS);
        }
        this.seatStatus = SeatStatus.PAID;
    }

    public boolean isAssignedByMember(Member loginMember) {
        return loginMember.equals(member);
    }
}
