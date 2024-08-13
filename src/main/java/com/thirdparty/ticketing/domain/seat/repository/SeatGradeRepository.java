package com.thirdparty.ticketing.domain.seat.repository;

import com.thirdparty.ticketing.domain.seat.SeatGrade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface SeatGradeRepository extends JpaRepository<SeatGrade, Integer> {
    @Query("""
            SELECT sg 
            FROM SeatGrade sg
            WHERE sg.performance.id = :performanceId 
            AND sg.seatGradeId IN :gradeIds
            """)
    List<SeatGrade> findByPerformanceIdAndGradeNames(
            @Param("performanceId") long performanceId,
            @Param("gradeIds") List<Long> gradeIds
    );
}
