package com.thirdparty.ticketing.domain.zone.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ZoneCreationElement {
    @NotBlank(message = "구역이름은 공백일 수 없습니다.")
    private String zoneName;
}
