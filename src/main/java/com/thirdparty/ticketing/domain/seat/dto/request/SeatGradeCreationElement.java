package com.thirdparty.ticketing.domain.seat.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.Data;

@Data
public class SeatGradeCreationElement {
    @NotNull(message = "가격 필드는 비어있을 수 없습니다.")
    @Min(value = 0, message = "가격은 0원 이상이어야 합니다.")
    private Long price;

    @NotBlank(message = "좌석 등급 명은 공백일 수 없습니다.")
    @Size(max = 32, message = "좌석 등급 명은 32자 이하로만 설정할 수 있습니다.")
    private String gradeName;
}
