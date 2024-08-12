package com.thirdparty.ticketing.domain.performance.controller;

import com.thirdparty.ticketing.domain.ItemResult;
import com.thirdparty.ticketing.domain.performance.dto.PerformanceElement;
import com.thirdparty.ticketing.domain.performance.service.MemberPerformanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/performances")
public class MemberPerformanceController {

    private final MemberPerformanceService memberPerformanceService;

    @GetMapping()
    public ItemResult<PerformanceElement> getPerformances() {
        return ItemResult.of(memberPerformanceService.getPerformances());
    }
}
