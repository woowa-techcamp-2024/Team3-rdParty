package com.thirdparty.ticketing.domain.zone.repository;

import com.thirdparty.ticketing.domain.zone.Zone;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ZoneRepository extends JpaRepository<Zone, Long> {
}
