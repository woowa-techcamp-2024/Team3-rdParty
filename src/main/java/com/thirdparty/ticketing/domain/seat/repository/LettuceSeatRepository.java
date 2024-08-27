package com.thirdparty.ticketing.domain.seat.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.thirdparty.ticketing.domain.seat.RedisSeat;

public interface LettuceSeatRepository extends CrudRepository<RedisSeat, Long> {
    Optional<RedisSeat> findBySeatId(Long seatId);
}
