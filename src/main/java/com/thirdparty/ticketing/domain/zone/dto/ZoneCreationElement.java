package com.thirdparty.ticketing.domain.zone.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ZoneCreationElement {
    @NotBlank
    private String zoneName;
}
