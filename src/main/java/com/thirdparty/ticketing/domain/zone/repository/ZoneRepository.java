package com.thirdparty.ticketing.domain.zone.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.thirdparty.ticketing.domain.zone.Zone;

public interface ZoneRepository extends JpaRepository<Zone, Long> {}
