package com.thirdparty.ticketing.domain.seat;

import com.thirdparty.ticketing.domain.common.ErrorCode;
import com.thirdparty.ticketing.domain.common.TicketingException;
import com.thirdparty.ticketing.domain.member.Member;

import lombok.Getter;

@Getter
public class RedisSeat {

    private Long seatId;
    private Long memberId; // 예약한 유저 정보 -> null이면 아직 없음
    private SeatStatus seatStatus; // 예약 상태 -> 예약 가능, 예약 불가능

    public RedisSeat(Long seatId, Long memberId, SeatStatus seatStatus) {
        this.seatId = seatId;
        this.memberId = memberId;
        this.seatStatus = seatStatus;
    }

    public void checkSelectable() {
        if (this.seatStatus != SeatStatus.SELECTABLE) {
            throw new TicketingException(ErrorCode.NOT_SELECTABLE_SEAT);
        }
    }

    public void assignByMember(Member member) {
        if (this.seatStatus != SeatStatus.SELECTABLE) {
            throw new TicketingException(ErrorCode.NOT_SELECTABLE_SEAT);
        }

        this.memberId = member.getMemberId();
        this.seatStatus = SeatStatus.SELECTED;
    }

    public boolean isAssignedByMember(Member loginMember) {
        return loginMember.getMemberId().equals(this.memberId)
                && this.seatStatus == SeatStatus.SELECTED;
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

    public void markAsSelected() {
        this.seatStatus = SeatStatus.SELECTED;
    }

    public void releaseSeat(Member loginMember) {
        if (!isAssignedByMember(loginMember)) {
            throw new TicketingException(ErrorCode.NOT_SELECTABLE_SEAT);
        }

        this.memberId = null;
        this.seatStatus = SeatStatus.SELECTABLE;
    }
}
