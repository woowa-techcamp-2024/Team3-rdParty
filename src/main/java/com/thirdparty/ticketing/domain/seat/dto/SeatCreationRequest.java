package com.thirdparty.ticketing.domain.seat.dto;

import lombok.Data;

import java.util.List;

@Data
public class SeatCreationRequest {
    private List<SeatCreationElement> seats;
}
