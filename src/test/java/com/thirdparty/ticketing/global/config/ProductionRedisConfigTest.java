package com.thirdparty.ticketing.global.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.thirdparty.ticketing.support.BaseIntegrationTest;

class ProductionRedisConfigTest extends BaseIntegrationTest {

    @Autowired private RedissonClient redissonClient;

    @Autowired private StringRedisTemplate redisTemplate;

    @Test
    void testRedissonClient() {
        assertThat(redissonClient).isNotNull();
    }

    @Test
    void testLettuceRedisTemplate() {
        assertThat(redisTemplate).isNotNull();
        redisTemplate.opsForValue().set("testKey", "testValue");
        String value = redisTemplate.opsForValue().get("testKey");
        assertThat(value).isEqualTo("testValue");
    }
}
