package com.thirdparty.ticketing.domain.seat.dto.response;

import com.thirdparty.ticketing.domain.seat.Seat;

import lombok.Data;

@Data
public class SeatElement {
    private final long seatId;
    private final String seatCode;
    private final boolean seatAvailable;

    public static SeatElement of(Seat seat) {
        boolean isSeatAvailable = seat.isSelectable();
        return new SeatElement(seat.getSeatId(), seat.getSeatCode(), isSeatAvailable);
    }
}
