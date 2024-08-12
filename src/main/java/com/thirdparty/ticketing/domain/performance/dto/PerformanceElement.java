package com.thirdparty.ticketing.domain.performance.dto;

import com.thirdparty.ticketing.domain.performance.Performance;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class PerformanceElement {

    @NotBlank(message = "공연 아이디는 공백일 수 없습니다.")
    private Long performanceId;

    @NotBlank(message = "공연 이름은 공백일 수 없습니다.")
    private String performanceName;

    @NotBlank(message = "공연 장소는 공백일 수 없습니다.")
    private String performancePlace;

    @NotNull(message = "공연 시간은 공백일 수 없습니다.")
    private ZonedDateTime performanceShowtime;

    public static PerformanceElement of (Performance performance) {
        PerformanceElement performanceElement = new PerformanceElement();
        performanceElement.setPerformanceId(performance.getPerformanceId());
        performanceElement.setPerformanceName(performance.getPerformanceName());
        performanceElement.setPerformancePlace(performance.getPerformancePlace());
        performanceElement.setPerformanceShowtime(performance.getPerformanceShowtime());
        return performanceElement;
    }
}
