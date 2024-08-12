package com.thirdparty.ticketing.domain.zone.dto;

import jakarta.validation.Valid;
import lombok.Data;

import java.util.List;

@Data
public class ZoneCreationRequest {
    @Valid
    private List<ZoneCreationElement> zones;
}
