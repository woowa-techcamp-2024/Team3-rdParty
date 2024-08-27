package com.thirdparty.ticketing.domain.ticket.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import lombok.Data;

@Data
public class TicketPaymentRequest {
    @NotNull(message = "좌석 ID를 요청하지 않았습니다.")
    @Min(value = 1, message = "좌석 ID는 1 이상이어야 합니다.")
    private Long seatId;
}
