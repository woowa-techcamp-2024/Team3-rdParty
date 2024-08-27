package com.thirdparty.ticketing.domain.seat.repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import com.thirdparty.ticketing.domain.common.ErrorCode;
import com.thirdparty.ticketing.domain.common.TicketingException;
import com.thirdparty.ticketing.domain.seat.RedisSeat;
import com.thirdparty.ticketing.domain.seat.SeatStatus;

@Repository
public class LettuceSeatRepository {
    private static final String SEAT_DATA_KEY = "seat-data-";
    private final HashOperations<String, String, String> seatData;

    public LettuceSeatRepository(StringRedisTemplate redisTemplate) {
        this.seatData = redisTemplate.opsForHash();
    }

    public Optional<RedisSeat> findBySeatId(Long seatId) {
        Map<String, String> seatMap = seatData.entries(getSeatDataKey(seatId));

        if (seatMap.isEmpty()) {
            throw new TicketingException(ErrorCode.NOT_FOUND_SEAT);
        }

        RedisSeat redisSeat =
                new RedisSeat(
                        Long.valueOf(seatMap.get("seatId")),
                        seatMap.get("memberId") != null
                                ? Long.valueOf(seatMap.get("memberId"))
                                : null,
                        SeatStatus.valueOf(seatMap.get("seatStatus")));

        return Optional.of(redisSeat);
    }

    public void update(RedisSeat redisSeat) {
        // 값 있는지 체크 안하고 하면 side effect 발생
        Map<String, String> seatMap = new HashMap<>();
        seatMap.put("seatId", redisSeat.getSeatId().toString());
        if (redisSeat.getMemberId() != null) {
            seatMap.put("memberId", redisSeat.getMemberId().toString());
        }
        seatMap.put("seatStatus", redisSeat.getSeatStatus().toString());

        seatData.putAll(getSeatDataKey(redisSeat.getSeatId()), seatMap);
    }

    private String getSeatDataKey(Long seatId) {
        return SEAT_DATA_KEY + seatId;
    }
}
