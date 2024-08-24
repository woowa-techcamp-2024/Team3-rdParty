package com.thirdparty.ticketing.domain.ticket.controller;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thirdparty.ticketing.domain.common.LoginMember;
import com.thirdparty.ticketing.domain.ticket.dto.sse.SeatEventResponse;

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

    @PostMapping("/performances/{performanceId}/seats/{seatId}/select")
    public void selectSeat(
            @LoginMember String memberEmail,
            @PathVariable("performanceId") Long performanceId,
            @PathVariable("seatId") Long seatId) {
        sendEventToPerformanceAsync(memberEmail, performanceId, "SELECT", seatId);
        log.debug(
                "좌석 선택 요청이 접수되었고 공연 ID: {}, 좌석 ID: {}에 대한 비동기 브로드캐스트가 시작되었습니다",
                performanceId,
                seatId);
    }

    @PostMapping("/performances/{performanceId}/seats/{seatId}/release")
    public void releaseSeat(
            @LoginMember String memberEmail,
            @PathVariable("performanceId") Long performanceId,
            @PathVariable("seatId") Long seatId) {
        sendEventToPerformanceAsync(memberEmail, performanceId, "RELEASE", seatId);
        log.debug(
                "좌석 해제 요청이 접수되었고 공연 ID: {}, 좌석 ID: {}에 대한 비동기 브로드캐스트가 시작되었습니다",
                performanceId,
                seatId);
    }

    @Async
    public void sendEventToPerformanceAsync(
            String memberEmail, Long performanceId, String eventName, Long seatId) {
        log.debug("공연 ID: {}, 이벤트: {}에 대한 비동기 브로드캐스트를 시작합니다", performanceId, eventName);
        ConcurrentMap<String, SseEmitter> performanceEmitters = emitters.get(performanceId);
        if (performanceEmitters != null) {
            String status = eventName.equals("SELECT") ? "SELECTED" : "SELECTABLE";
            SeatEventResponse eventData = new SeatEventResponse(seatId, status);
            performanceEmitters.forEach(
                    (email, emitter) -> {
                        if (email.equals(memberEmail)) {
                            log.debug("공연 ID: {}, 이메일: {}로는 이벤트를 전송하지 않음", performanceId, email);
                            return;
                        }
                        try {
                            String jsonData = objectMapper.writeValueAsString(eventData);
                            emitter.send(SseEmitter.event().name(eventName).data(jsonData));
                            log.debug(
                                    "공연 ID: {}, 이미터 ID: {}로 이벤트가 전송되었습니다. 이벤트: {}, 상태: {}",
                                    performanceId,
                                    email,
                                    eventName,
                                    status);
                        } catch (Exception e) {
                            log.error(
                                    "공연 ID: {}, 이미터 ID: {}로 이벤트 전송 중 오류 발생",
                                    performanceId,
                                    email,
                                    e);
                            removeEmitter(performanceId, email);
                        }
                    });
        }
        log.debug("공연 ID: {}, 이벤트: {}에 대한 비동기 브로드캐스트가 완료되었습니다", performanceId, eventName);
    }
}
