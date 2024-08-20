package com.thirdparty.ticketing.support;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public class TestContainerStarter {

    private static final String REDIS_IMAGE = "redis:7.4.0";
    private static final int REDIS_PORT = 6379;
    private static final GenericContainer<?> REDIS;

    static {
        REDIS = new GenericContainer<>(REDIS_IMAGE).withExposedPorts(REDIS_PORT).withReuse(true);
        REDIS.start();
    }

    @DynamicPropertySource
    private static void registerRedisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", REDIS::getHost);
        registry.add("spring.data.redis.port", () -> REDIS.getMappedPort(REDIS_PORT).toString());
    }
}
