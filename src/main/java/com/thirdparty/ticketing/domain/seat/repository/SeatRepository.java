package com.thirdparty.ticketing.domain.seat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.thirdparty.ticketing.domain.seat.Seat;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {}
