package com.thirdparty.ticketing.domain.seat.dto;

import jakarta.validation.Valid;
import lombok.Data;

import java.util.List;

@Data
public class SeatCreationRequest {
    @Valid
    private List<SeatCreationElement> seats;
}
