package com.thirdparty.ticketing.domain.waiting;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thirdparty.ticketing.domain.common.LoginMember;
import com.thirdparty.ticketing.domain.waiting.manager.WaitingManager;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class WaitingController {

    private final WaitingManager waitingManager;

    @GetMapping("/performances/{performanceId}/wait")
    public ResponseEntity<Map<String, Long>> getCounts(
            @LoginMember String email, @PathVariable("performanceId") Long performanceId) {
        long remainingCount = waitingManager.getRemainingCount(email, performanceId);
        return ResponseEntity.ok(Map.of("remainingCount", remainingCount));
    }
}
