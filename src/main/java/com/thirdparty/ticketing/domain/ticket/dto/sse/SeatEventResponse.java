package com.thirdparty.ticketing.domain.ticket.dto.sse;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SeatEventResponse {
    private Long seatId;
    private String status;
}
