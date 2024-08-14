package com.thirdparty.ticketing.domain.seat.dto.response;

import com.thirdparty.ticketing.domain.seat.SeatGrade;

import lombok.Data;

@Data
public class SeatGradeElement {
    private final Long gradeId;
    private final String gradeName;
    private final Long price;

    public static SeatGradeElement of(SeatGrade seatGrade) {
        return new SeatGradeElement(
                seatGrade.getSeatGradeId(), seatGrade.getGradeName(), seatGrade.getPrice());
    }
}
