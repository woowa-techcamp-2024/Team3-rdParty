package com.thirdparty.ticketing.domain.performance.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.thirdparty.ticketing.domain.performance.Performance;

public interface PerformanceRepository extends JpaRepository<Performance, Long> {}
