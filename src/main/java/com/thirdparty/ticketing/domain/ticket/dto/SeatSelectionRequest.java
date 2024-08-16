package com.thirdparty.ticketing.domain.ticket.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import lombok.Data;

@Data
public class SeatSelectionRequest {
    @NotNull(message = "좌석 ID를 요청하지 않았습니다.")
    @Min(value = 1, message = "좌석 ID는 1 이상이어야 합니다.")
    private final Long seatId;
}
