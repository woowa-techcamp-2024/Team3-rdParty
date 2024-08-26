package com.thirdparty.ticketing.seat.dto.request;

import java.util.List;

import jakarta.validation.Valid;

import lombok.Data;

@Data
public class SeatGradeCreationRequest {
    @Valid private List<SeatGradeCreationElement> seatGrades;
}
