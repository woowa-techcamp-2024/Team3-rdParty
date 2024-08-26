package com.thirdparty.ticketing.ticket.service.strategy;

import java.util.Optional;

import com.thirdparty.ticketing.jpa.seat.Seat;
import com.thirdparty.ticketing.jpa.seat.repository.SeatRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PessimisticLockSeatStrategy implements LockSeatStrategy {
    private final SeatRepository seatRepository;

    @Override
    public Optional<Seat> getSeatWithLock(Long seatId) {
        return seatRepository.findByIdWithPessimistic(seatId);
    }
}
