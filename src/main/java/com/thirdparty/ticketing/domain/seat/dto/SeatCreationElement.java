package com.thirdparty.ticketing.domain.seat.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Data;

@Data
public class SeatCreationElement {
    @NotBlank(message = "좌석코드는 공백일 수 없습니다.")
    private String seatCode;

    @NotNull(message = "좌석 등급을 지정해야 합니다.")
    @Min(value = 1L, message = "좌석 등급 id는 1 이상 이어야 합니다.")
    private Long gradeId;
}
