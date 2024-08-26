package com.thirdparty.ticketing.ticket.dto.response;

import com.thirdparty.ticketing.jpa.seat.Seat;
import com.thirdparty.ticketing.seat.dto.response.SeatGradeElement;

import lombok.Data;

@Data
public class TicketSeatDetail {
    private final long seatId;
    private final String seatCode;
    private final SeatGradeElement grade;

    public static TicketSeatDetail of(Seat seat) {
        return new TicketSeatDetail(
                seat.getSeatId(), seat.getSeatCode(), SeatGradeElement.of(seat.getSeatGrade()));
    }
}
