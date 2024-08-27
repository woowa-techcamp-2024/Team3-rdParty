package com.thirdparty.ticketing.domain.ticket.service;

import com.thirdparty.ticketing.domain.member.Member;

public interface ReservationManager {
    void releaseSeat(Member loginMember, long seatId);
}
