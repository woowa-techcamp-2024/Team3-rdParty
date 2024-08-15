package com.thirdparty.ticketing.domain.ticket.policy;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.thirdparty.ticketing.domain.seat.Seat;
import com.thirdparty.ticketing.domain.seat.repository.SeatRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PessimisticLockSeatStrategy implements LockSeatStrategy {
    private final SeatRepository seatRepository;

    @Override
    public Optional<Seat> getSeatWithLock(Long seatId) {
        return seatRepository.findByIdWithPessimistic(seatId);
    }
}
