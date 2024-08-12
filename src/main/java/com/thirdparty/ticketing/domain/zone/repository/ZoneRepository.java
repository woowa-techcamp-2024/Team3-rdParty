package com.thirdparty.ticketing.domain.zone.repository;

import com.thirdparty.ticketing.domain.performance.Performance;
import com.thirdparty.ticketing.domain.zone.Zone;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ZoneRepository extends JpaRepository<Zone, Long> {

    List<Zone> findByPerformance(Performance performance);
}
