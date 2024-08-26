package com.thirdparty.ticketing.jpa.zone.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.thirdparty.ticketing.jpa.performance.Performance;
import com.thirdparty.ticketing.jpa.zone.Zone;

public interface ZoneRepository extends JpaRepository<Zone, Long> {

    List<Zone> findByPerformance(Performance performance);
}
