package com.thirdparty.ticketing.support;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public class TestContainerStarter {

    private static final String REDIS_IMAGE = "redis:7.4.0";
    private static final int REDIS_PORT = 6379;
    private static final GenericContainer<?> REDIS;

    private static final String MYSQL_IMAGE = "mysql:8.0.32";
    private static final int MYSQL_PORT = 3306;
    private static final MySQLContainer<?> MYSQL;

    static {
        REDIS = new GenericContainer<>(REDIS_IMAGE).withExposedPorts(REDIS_PORT).withReuse(true);
        REDIS.start();

        MYSQL =
                new MySQLContainer<>(MYSQL_IMAGE)
                        .withExposedPorts(MYSQL_PORT)
                        .withUsername("root")
                        .withPassword("root")
                        .withDatabaseName("ticketing")
                        .withReuse(false);
        MYSQL.start();
    }

    @DynamicPropertySource
    private static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", REDIS::getHost);
        registry.add("spring.data.redis.port", () -> REDIS.getMappedPort(REDIS_PORT).toString());

        String mysqlUrl =
                String.format(
                        "jdbc:mysql://%s:%s/ticketing",
                        MYSQL.getHost(), MYSQL.getMappedPort(MYSQL_PORT));

        registry.add("spring.datasource.url", () -> mysqlUrl);
        registry.add("spring.datasource.username", () -> "root");
        registry.add("spring.datasource.password", () -> "root");
    }
}
