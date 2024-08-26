package com.thirdparty.ticketing.seat.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SeatEventResponse {
    private Long seatId;
    private String status;
}
