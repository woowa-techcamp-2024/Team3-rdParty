package com.thirdparty.ticketing.domain.seat.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SeatCreationElement {
    @NotBlank(message = "좌석코드는 공백일 수 없습니다.")
    private String seatCode;
}
