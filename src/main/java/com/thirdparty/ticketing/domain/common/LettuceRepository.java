package com.thirdparty.ticketing.domain.common;

import java.time.Duration;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class LettuceRepository {
    private final StringRedisTemplate redisTemplate;

    // 1. lock을 생성
    // 2. 60초가 유지되는 key는 자리번호 value는 유저 id를 생성
    public Boolean seatLock(String key) {
        return redisTemplate.opsForValue().setIfAbsent(key, "lock", Duration.ofMinutes(1));
    }

    public Boolean couponLock(String key) {
        return redisTemplate.opsForValue().setIfAbsent(key, "lock", Duration.ofMinutes(1));
    }

    public void unlock(String string) {
        redisTemplate.delete(string);
    }
}
