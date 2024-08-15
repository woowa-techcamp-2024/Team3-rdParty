package com.thirdparty.ticketing.global.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.redisson.spring.data.connection.RedissonConnectionFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

import lombok.Setter;

@Configuration
@ConfigurationProperties(prefix = "spring.data.redis.redisson")
@Setter
public class RedissonConfig {
    private String host;
    private Integer port;

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        SingleServerConfig serverConfig = config.useSingleServer();
        serverConfig.setAddress("redis://" + host + ":" + port);
        return Redisson.create(config);
    }

    @Bean
    public StringRedisTemplate redissonRedisTemplate(RedissonClient redissonClient) {
        StringRedisTemplate redisTemplate = new StringRedisTemplate();
        redisTemplate.setConnectionFactory(new RedissonConnectionFactory(redissonClient));
        return redisTemplate;
    }
}
