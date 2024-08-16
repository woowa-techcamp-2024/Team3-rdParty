package com.thirdparty.ticketing.domain.ticket.policy;

import java.util.Optional;

import com.thirdparty.ticketing.domain.seat.Seat;
import com.thirdparty.ticketing.domain.seat.repository.SeatRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class NaiveSeatStrategy implements LockSeatStrategy {
    private final SeatRepository seatRepository;

    @Override
    public Optional<Seat> getSeatWithLock(Long seatId) {
        return seatRepository.findById(seatId);
    }
}
