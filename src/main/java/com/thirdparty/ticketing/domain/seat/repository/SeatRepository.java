package com.thirdparty.ticketing.domain.seat.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.thirdparty.ticketing.domain.seat.Seat;
import com.thirdparty.ticketing.domain.zone.Zone;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {
    List<Seat> findByZone(Zone zone);
}
