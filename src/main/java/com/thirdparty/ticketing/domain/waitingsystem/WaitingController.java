package com.thirdparty.ticketing.domain.waitingsystem;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.thirdparty.ticketing.domain.common.LoginMember;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class WaitingController {

    private final WaitingSystem waitingSystem;

    @GetMapping("/performances/{performanceId}/wait")
    public ResponseEntity<Map<String, Long>> getRemainingCount(
            @LoginMember String email, @PathVariable("performanceId") Long performanceId) {
        long remainingCount = waitingSystem.getRemainingCount(email, performanceId);
        return ResponseEntity.ok(Map.of("remainingCount", remainingCount));
    }
}
