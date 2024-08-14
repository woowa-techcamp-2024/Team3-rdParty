package com.thirdparty.ticketing.global.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

@SpringBootTest(
        properties = {
            "spring.data.redis.redisson.host=localhost",
            "spring.data.redis.redisson.port=6379"
        })
class RedissonConfigTest {

    @Autowired private RedissonClient redissonClient;

    @Autowired private StringRedisTemplate redissonRedisTemplate;

    @Test
    void testRedissonClient() {
        assertThat(redissonClient).isNotNull();
    }

    @Test
    void testRedissonRedisTemplate() {
        assertThat(redissonRedisTemplate).isNotNull();
        redissonRedisTemplate.opsForValue().set("testKey", "testValue");
        String value = redissonRedisTemplate.opsForValue().get("testKey");
        assertThat(value).isEqualTo("testValue");
    }
}
