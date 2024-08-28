package com.thirdparty.ticketing.global.config;

import java.time.Duration;
import java.util.Iterator;
import java.util.List;

import org.springframework.data.domain.Range;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.PendingMessages;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.connection.stream.StreamInfo;
import org.springframework.data.redis.connection.stream.StreamInfo.XInfoGroup;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.stereotype.Component;

import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.codec.StringCodec;
import io.lettuce.core.output.StatusOutput;
import io.lettuce.core.protocol.CommandArgs;
import io.lettuce.core.protocol.CommandKeyword;
import io.lettuce.core.protocol.CommandType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class RedisOperator {
    private final RedisConnectionFactory redisConnectionFactory;
    private final StringRedisTemplate redisTemplate;

    public boolean isStreamConsumerGroupExist(String streamKey, String consumerGroupName) {
        Iterator<XInfoGroup> iterator =
                this.redisTemplate.opsForStream().groups(streamKey).stream().iterator();

        while (iterator.hasNext()) {
            StreamInfo.XInfoGroup xInfoGroup = iterator.next();
            if (xInfoGroup.groupName().equals(consumerGroupName)) {
                return true;
            }
        }
        return false;
    }

    public void createStreamConsumerGroup(String streamKey, String consumerGroupName) {
        // if stream is not exist, create stream and consumer group of it
        if (Boolean.FALSE.equals(this.redisTemplate.hasKey(streamKey))) {
            RedisAsyncCommands commands =
                    (RedisAsyncCommands)
                            redisConnectionFactory.getConnection().getNativeConnection();

            CommandArgs<String, String> args =
                    new CommandArgs<>(StringCodec.UTF8)
                            .add(CommandKeyword.CREATE)
                            .add(streamKey)
                            .add(consumerGroupName)
                            .add("0")
                            .add("MKSTREAM");

            commands.dispatch(CommandType.XGROUP, new StatusOutput(StringCodec.UTF8), args);
        }
        // stream is exist, create consumerGroup if is not exist
        else {
            if (!isStreamConsumerGroupExist(streamKey, consumerGroupName)) {
                this.redisTemplate
                        .opsForStream()
                        .createGroup(streamKey, ReadOffset.from("0"), consumerGroupName);
            }
        }
    }

    public StreamMessageListenerContainer createStreamMessageListenerContainer() {
        return StreamMessageListenerContainer.create(
                redisConnectionFactory,
                StreamMessageListenerContainer.StreamMessageListenerContainerOptions.builder()
                        .hashKeySerializer(new StringRedisSerializer())
                        .hashValueSerializer(new StringRedisSerializer())
                        .pollTimeout(Duration.ofMillis(20))
                        .build());
    }

    public void acknowledge(String consumerGroup, MapRecord<String, Object, Object> message) {
        Long ack = this.redisTemplate.opsForStream().acknowledge(consumerGroup, message);
        if (ack == 0) {
            log.error("Acknowledge failed. MessageId: {}", message.getId());
        } else {
            log.info("Acknowledge success. MessageId: {}", message.getId());
        }
    }

    public PendingMessages getPendingMessage(
            String streamKey, String consumerGroup, String consumerName) {
        return this.redisTemplate
                .opsForStream()
                .pending(
                        streamKey,
                        Consumer.from(consumerGroup, consumerName),
                        Range.unbounded(),
                        100L);
    }

    public List<MapRecord<String, Object, Object>> claim(
            String streamKey,
            String consumerGroup,
            String consumerName,
            Duration minIdleTime,
            RecordId... messageIds) {
        if (messageIds.length < 1) {
            return List.of();
        }

        return this.redisTemplate
                .opsForStream()
                .claim(streamKey, consumerGroup, consumerName, minIdleTime, messageIds);
    }
}
