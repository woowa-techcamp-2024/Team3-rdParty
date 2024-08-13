package com.thirdparty.ticketing.domain.zone.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.thirdparty.ticketing.domain.performance.Performance;
import com.thirdparty.ticketing.domain.zone.Zone;

public interface ZoneRepository extends JpaRepository<Zone, Long> {

    List<Zone> findByPerformance(Performance performance);
}
