package com.thirdparty.ticketing.domain.ticket.policy;

import java.util.Optional;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.thirdparty.ticketing.domain.seat.Seat;
import com.thirdparty.ticketing.domain.seat.repository.SeatRepository;

import lombok.RequiredArgsConstructor;

@Primary
@Component
@RequiredArgsConstructor
public class OptimisticLockSeatStrategy implements LockSeatStrategy {
    private final SeatRepository seatRepository;

    @Override
    public Optional<Seat> getSeatWithLock(Long seatId) {
        return seatRepository.findByIdWithOptimistic(seatId);
    }
}
