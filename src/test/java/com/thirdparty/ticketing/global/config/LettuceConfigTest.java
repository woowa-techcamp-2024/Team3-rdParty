package com.thirdparty.ticketing.global.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.thirdparty.ticketing.support.TestContainerStarter;

@SpringBootTest
class LettuceConfigTest extends TestContainerStarter {

    @Autowired private StringRedisTemplate lettuceRedisTemplate;

    @Test
    void testLettuceRedisTemplate() {
        assertThat(lettuceRedisTemplate).isNotNull();
        lettuceRedisTemplate.opsForValue().set("testKey", "testValue");
        String value = lettuceRedisTemplate.opsForValue().get("testKey");
        assertThat(value).isEqualTo("testValue");
    }
}
