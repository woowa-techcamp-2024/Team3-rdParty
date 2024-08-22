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

import io.lettuce.core.dynamic.annotation.Param;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {
    List<Seat> findByZone(Zone zone);

    @Query("SELECT s FROM Seat as s WHERE s.seatId = :seatId")
    @Lock(LockModeType.NONE)
    Optional<Seat> findById(@Param("seatId") Long seatId);

    @Query("SELECT s FROM Seat as s WHERE s.seatId = :seatId")
    @Lock(LockModeType.OPTIMISTIC)
    Optional<Seat> findByIdWithOptimistic(@Param("seatId") Long seatId);

    @Query("SELECT s FROM Seat as s WHERE s.seatId = :seatId")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Seat> findByIdWithPessimistic(@Param("seatId") Long seatId);

    @Query(
            """
			SELECT s FROM Seat s
			JOIN FETCH s.zone z
			JOIN FETCH z.performance p
			WHERE p.performanceId = :performanceId
			""")
    List<Seat> findByPerformanceId(@Param("performanceId") long performanceId);
}
