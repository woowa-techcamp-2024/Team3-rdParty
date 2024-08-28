package com.thirdparty.ticketing.global.config;

import java.time.Duration;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.data.redis.stream.Subscription;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedisStreamConsumer
        implements StreamListener<String, MapRecord<String, Object, Object>> {

    private final RedisOperator redisOperator;
    private final RedisStreamConfig redisStreamConfig;
    private final MessageRouterService messageRouterService;

    private StreamMessageListenerContainer<String, MapRecord<String, Object, Object>>
            listenerContainer;
    private Subscription subscription;

    @Override
    public void onMessage(MapRecord<String, Object, Object> message) {
        messageRouterService.consume(
                message,
                () -> redisOperator.acknowledge(redisStreamConfig.getConsumerGroupName(), message));
    }

    @PostConstruct
    public void init() throws InterruptedException {
        // Consumer Group 설정
        this.redisOperator.createStreamConsumerGroup(
                redisStreamConfig.getStreamKey(), redisStreamConfig.getConsumerGroupName());

        // StreamMessageListenerContainer 설정
        this.listenerContainer = this.redisOperator.createStreamMessageListenerContainer();

        // Subscription 설정
        this.subscription =
                this.listenerContainer.receive(
                        Consumer.from(
                                redisStreamConfig.getConsumerGroupName(),
                                redisStreamConfig.getConsumerName()),
                        StreamOffset.create(
                                redisStreamConfig.getStreamKey(), ReadOffset.lastConsumed()),
                        this);

        // redis stream 구독 생성까지 Blocking 된다. 이때의 timeout 2초다. 만약 2초보다 빠르게 구독이 생성되면 바로 다음으로 넘어간다.
        this.subscription.await(Duration.ofSeconds(2));

        // redis listen 시작
        this.listenerContainer.start();
    }

    @PreDestroy
    public void destroy() {
        if (this.subscription != null) {
            this.subscription.cancel();
        }
        if (this.listenerContainer != null) {
            this.listenerContainer.stop();
        }
    }
}
