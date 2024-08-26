package com.thirdparty.ticketing.redis.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

@Profile("prod")
@Configuration
public class ProductionRedisConfig {

    private final int port;
    private final String host;
    private final String password;

    public ProductionRedisConfig(
            @Value("${spring.data.redis.port}") int port,
            @Value("${spring.data.redis.host}") String host,
            @Value("${spring.data.redis.password}") String password) {
        this.port = port;
        this.host = host;
        this.password = password;
    }

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        SingleServerConfig serverConfig = config.useSingleServer();
        serverConfig.setAddress("redis://" + host + ":" + port);
        serverConfig.setPassword(password);
        return Redisson.create(config);
    }

    @Bean
    public LettuceConnectionFactory lettuceConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(host);
        config.setPort(port);
        config.setPassword(password);
        return new LettuceConnectionFactory(config);
    }
}
