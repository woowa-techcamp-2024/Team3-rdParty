package com.thirdparty.ticketing.global.config;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;

@Configuration
public class RedisStreamConfig {

    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.port}")
    private String redisPort;

    @Value("${spring.data.redis.password}")
    private String redisPassword;

    @Getter
    @Value("${stream.key}")
    private String streamKey;

    @Getter
    @Value("${stream.consumer.groupName}")
    private String consumerGroupName;

    @Getter private String consumerName = UUID.randomUUID().toString();
}
