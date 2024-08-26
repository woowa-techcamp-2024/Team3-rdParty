package com.thirdparty.ticketing.waiting;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thirdparty.ticketing.security.annotation.LoginMember;
import com.thirdparty.ticketing.waiting.waitingsystem.WaitingSystem;

import lombok.RequiredArgsConstructor;

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

    @DeleteMapping("/performances/{performanceId}/wait")
    public ResponseEntity<Void> removeMemberInfo(
            @LoginMember String email, @PathVariable("performanceId") Long performanceId) {
        waitingSystem.pullOutRunningMember(email, performanceId);
        return ResponseEntity.noContent().build();
    }
}
