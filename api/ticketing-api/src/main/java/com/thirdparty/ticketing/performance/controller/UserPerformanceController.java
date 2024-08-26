package com.thirdparty.ticketing.performance.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thirdparty.common.ItemResult;
import com.thirdparty.ticketing.performance.dto.PerformanceElement;
import com.thirdparty.ticketing.performance.service.UserPerformanceService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/performances")
public class UserPerformanceController {

    private final UserPerformanceService userPerformanceService;

    @GetMapping()
    public ResponseEntity<ItemResult<PerformanceElement>> getPerformances() {
        return ResponseEntity.ok(userPerformanceService.getPerformances());
    }

    @GetMapping("/{performanceId}")
    public ResponseEntity<PerformanceElement> getPerformance(
            @PathVariable("performanceId") long performanceId) {
        return ResponseEntity.ok(userPerformanceService.getPerformance(performanceId));
    }
}
