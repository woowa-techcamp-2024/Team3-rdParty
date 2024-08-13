package com.thirdparty.ticketing.domain.seat.dto;

import com.thirdparty.ticketing.domain.seat.Seat;
import com.thirdparty.ticketing.domain.seat.SeatStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SeatElement {
    private final long seatId;
    private final String seatCode;
    private final boolean isSeatAvailable;

    public static SeatElement of(Seat seat) {
        return SeatElement.builder()
                .seatId(seat.getSeatId())
                .seatCode(seat.getSeatCode())
                .isSeatAvailable(seat.getSeatStatus().equals(SeatStatus.AVAILABLE))
                .build();
    }
}
