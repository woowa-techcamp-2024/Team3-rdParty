package com.thirdparty.ticketing.domain.seat.repository;

import com.thirdparty.ticketing.domain.seat.SeatGrade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface SeatGradeRepository extends JpaRepository<SeatGrade, Integer> {
    List<SeatGrade> findByPerformanceIdAndGradeNameIn(long performanceId, List<String> gradeNames);
}
