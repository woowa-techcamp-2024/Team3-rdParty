package com.thirdparty.ticketing.domain.performance.dto.request;

import java.time.ZonedDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Data;

@Data
public class PerformanceCreationRequest {

    @NotBlank(message = "공연 이름은 공백일 수 없습니다.")
    private String performanceName;

    @NotBlank(message = "공연 장소는 공백일 수 없습니다.")
    private String performancePlace;

    @NotNull(message = "공연 시간은 반드시 입력해야 합니다.")
    private ZonedDateTime performanceShowtime;
}
