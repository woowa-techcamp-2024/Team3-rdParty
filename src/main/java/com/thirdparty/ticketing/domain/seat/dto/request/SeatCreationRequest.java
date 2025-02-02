package com.thirdparty.ticketing.domain.seat.dto.request;

import java.util.List;

import jakarta.validation.Valid;

import lombok.Data;

@Data
public class SeatCreationRequest {
    @Valid private List<SeatCreationElement> seats;
}
