package com.thirdparty.ticketing.global.config;

import java.util.Map;

import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageRouterService {

    private final SeatSynchronizer seatSynchronizer;

    @Transactional
    public void consume(MapRecord<String, Object, Object> mapRecord, Runnable postProcess) {
        // 왜 실행 안됨?
        System.out.println("MessageId: {}" + mapRecord.getId());
        System.out.println("Stream: {}" + mapRecord.getStream());
        System.out.println("Body: {}" + mapRecord.getValue());

        log.info("Received message: {}", mapRecord.getStream());

        Map<Object, Object> message = mapRecord.getValue();
        for (Object type : message.keySet()) {
            String messageType = (String) type;
            Long seatId = Long.valueOf((String) message.get(messageType));

            switch (messageType) {
                case "occupy":
                    seatSynchronizer.occupy(seatId);
                    break;
                case "release":
                    seatSynchronizer.release(seatId);
                    break;
                default:
                    log.warn("Unknown message type: {}", messageType);
            }
        }
        postProcess.run();
    }
}
