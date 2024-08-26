package com.thirdparty.ticketing.redis.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.thirdparty.ticketing.testcontainer.RedisTestContainerStarter;

@SpringBootTest
class ProductionRedisConfigTest extends RedisTestContainerStarter {

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
