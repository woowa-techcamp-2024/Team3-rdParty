package com.thirdparty.ticketing.domain.seat.dto;

import jakarta.validation.Valid;
import lombok.Data;

import java.util.List;

@Data
public class SeatGradeCreationRequest {
    @Valid
    private List<SeatGradeCreationElement> seatGrades;
}
