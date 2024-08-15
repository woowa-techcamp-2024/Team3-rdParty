package com.thirdparty.ticketing.domain.ticket.policy;

import java.util.Optional;

import com.thirdparty.ticketing.domain.seat.Seat;

public interface LockSeatStrategy {
    Optional<Seat> getSeatWithLock(Long seatId);
}
