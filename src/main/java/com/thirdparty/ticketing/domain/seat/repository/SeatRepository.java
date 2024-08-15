package com.thirdparty.ticketing.domain.seat.repository;

import java.util.List;
import java.util.Optional;

import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.thirdparty.ticketing.domain.seat.Seat;
import com.thirdparty.ticketing.domain.zone.Zone;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {
    List<Seat> findByZone(Zone zone);

    @Query("SELECT s FROM Seat as s WHERE s.id = :seatId")
    @Lock(LockModeType.OPTIMISTIC)
    Optional<Seat> findByIdWithOptimistic(Long seatId);

    @Query("SELECT s FROM Seat as s WHERE s.id = :seatId")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Seat> findByIdWithPessimistic(Long seatId);
}
