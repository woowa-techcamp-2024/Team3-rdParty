package com.thirdparty.ticketing.domain.performance.repository;

import com.thirdparty.ticketing.domain.performance.Performance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PerformanceRepository extends JpaRepository<Performance, Long> {
}
