package com.thirdparty.ticketing.domain.performance.controller;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thirdparty.ticketing.domain.performance.dto.request.PerformanceCreationRequest;
import com.thirdparty.ticketing.domain.performance.service.AdminPerformanceService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/performances")
public class AdminPerformanceController {

    private final AdminPerformanceService adminPerformanceService;

    @PostMapping
    public ResponseEntity<Void> createPerformance(
            @RequestBody @Valid PerformanceCreationRequest performanceCreationRequest) {
        adminPerformanceService.createPerformance(performanceCreationRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
