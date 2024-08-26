package com.thirdparty.ticketing.ticket.controller;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thirdparty.ticketing.event.dto.SeatEvent;
import com.thirdparty.ticketing.seat.dto.response.SeatEventResponse;
import com.thirdparty.ticketing.security.annotation.LoginMember;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TicketSseController {

    private final ConcurrentMap<Long, ConcurrentMap<String, SseEmitter>> emitters =
            new ConcurrentHashMap<>();

    private final ObjectMapper objectMapper;

    @GetMapping("/subscribe/performances/{performanceId}")
    public SseEmitter subscribePerformances(
            @LoginMember String memberEmail, @PathVariable("performanceId") Long performanceId) {
        SseEmitter emitter = new SseEmitter(300000L);

        ConcurrentMap<String, SseEmitter> performanceEmitters =
                emitters.computeIfAbsent(performanceId, k -> new ConcurrentHashMap<>());
        performanceEmitters.put(memberEmail, emitter);

        emitter.onCompletion(() -> removeEmitter(performanceId, memberEmail));
        emitter.onTimeout(() -> removeEmitter(performanceId, memberEmail));

        try {
            emitter.send(SseEmitter.event().name("INIT").data("공연 " + performanceId + "에 연결되었습니다"));
        } catch (IOException e) {
            removeEmitter(performanceId, memberEmail);
        }

        return emitter;
    }

    private void removeEmitter(Long performanceId, String memberEmail) {
        ConcurrentMap<String, SseEmitter> performanceEmitters = emitters.get(performanceId);
        log.debug("이미터 제거 - 공연 ID: {}, 이미터 ID: {}", performanceId, memberEmail);
        if (performanceEmitters != null) {
            performanceEmitters.remove(memberEmail);
            if (performanceEmitters.isEmpty()) {
                emitters.remove(performanceId);
                log.debug("공연 ID: {}에 대한 모든 이미터가 제거되었습니다", performanceId);
            }
        }
    }

    public void sendEventToPerformance(SeatEvent event) {
        getPerformanceIdFromRequest()
                .ifPresentOrElse(
                        performanceId -> {
                            log.debug(
                                    "공연 ID: {}, 이벤트: {}에 대한 브로드캐스트를 시작합니다",
                                    performanceId,
                                    event.getEventType());
                            Optional.ofNullable(emitters.get(performanceId))
                                    .ifPresent(
                                            performanceEmitters -> {
                                                String status =
                                                        event.getEventType()
                                                                        .equals(
                                                                                SeatEvent.EventType
                                                                                        .SELECT)
                                                                ? "SELECTED"
                                                                : "SELECTABLE";
                                                performanceEmitters.forEach(
                                                        (email, emitter) -> {
                                                            if (!email.equals(
                                                                    event.getMemberEmail())) {
                                                                sendEventToEmitter(
                                                                        performanceId,
                                                                        event,
                                                                        email,
                                                                        emitter,
                                                                        status);
                                                            } else {
                                                                log.debug(
                                                                        "공연 ID: {}, 이메일: {}로는 이벤트를 전송하지 않음",
                                                                        performanceId,
                                                                        email);
                                                            }
                                                        });
                                            });
                            log.debug(
                                    "공연 ID: {}, 이벤트: {}에 대한 브로드캐스트가 완료되었습니다",
                                    performanceId,
                                    event.getEventType());
                        },
                        () -> log.error("performanceId를 찾을 수 없습니다."));
    }

    private void sendEventToEmitter(
            Long performanceId, SeatEvent event, String email, SseEmitter emitter, String status) {
        try {
            SeatEventResponse eventData = new SeatEventResponse(event.getSeatId(), status);
            String jsonData = objectMapper.writeValueAsString(eventData);
            emitter.send(SseEmitter.event().name(event.getEventType().toString()).data(jsonData));
            log.debug(
                    "공연 ID: {}, 이미터 ID: {}로 이벤트가 전송되었습니다. 이벤트: {}, 상태: {}",
                    performanceId,
                    email,
                    event.getEventType(),
                    status);
        } catch (Exception e) {
            log.error("공연 ID: {}, 이미터 ID: {}로 이벤트 전송 중 오류 발생", performanceId, email, e);
            removeEmitter(performanceId, email);
        }
    }

    private Optional<Long> getPerformanceIdFromRequest() {
        return Optional.ofNullable(RequestContextHolder.getRequestAttributes())
                .filter(ServletRequestAttributes.class::isInstance)
                .map(ServletRequestAttributes.class::cast)
                .map(ServletRequestAttributes::getRequest)
                .map(request -> request.getHeader("performanceId"))
                .flatMap(this::parsePerformanceId);
    }

    private Optional<Long> parsePerformanceId(String performanceIdString) {
        try {
            return Optional.of(Long.parseLong(performanceIdString));
        } catch (NumberFormatException e) {
            log.error("performanceId 헤더 값을 Long으로 변환할 수 없습니다: {}", performanceIdString);
            return Optional.empty();
        }
    }
}
