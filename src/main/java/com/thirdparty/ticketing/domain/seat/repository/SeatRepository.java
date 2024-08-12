package com.thirdparty.ticketing.domain.seat.repository;

import com.thirdparty.ticketing.domain.seat.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {
}
