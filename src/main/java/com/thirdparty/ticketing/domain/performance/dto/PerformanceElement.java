package com.thirdparty.ticketing.domain.performance.dto;

import java.time.ZonedDateTime;

import com.thirdparty.ticketing.domain.performance.Performance;

import lombok.Data;

@Data
public class PerformanceElement {

    private final Long performanceId;

    private final String performanceName;

    private final String performancePlace;

    private final ZonedDateTime performanceShowtime;

    public static PerformanceElement of(Performance perf) {
        return new PerformanceElement(
                perf.getPerformanceId(),
                perf.getPerformanceName(),
                perf.getPerformancePlace(),
                perf.getPerformanceShowtime());
    }
}
