package com.thirdparty.ticketing.ticket.service.strategy;

import java.util.Optional;

import com.thirdparty.ticketing.jpa.seat.Seat;

public interface LockSeatStrategy {
    Optional<Seat> getSeatWithLock(Long seatId);
}
