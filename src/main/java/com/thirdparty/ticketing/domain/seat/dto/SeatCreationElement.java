package com.thirdparty.ticketing.domain.seat.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SeatCreationElement {
    @NotBlank
    private String seatCode;
}
