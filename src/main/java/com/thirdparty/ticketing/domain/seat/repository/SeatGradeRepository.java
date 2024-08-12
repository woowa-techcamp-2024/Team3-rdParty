package com.thirdparty.ticketing.domain.seat.repository;

import com.thirdparty.ticketing.domain.seat.SeatGrade;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeatGradeRepository extends JpaRepository<SeatGrade, Integer> {
}
