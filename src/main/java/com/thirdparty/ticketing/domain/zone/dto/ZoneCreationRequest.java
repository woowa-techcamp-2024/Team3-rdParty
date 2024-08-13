package com.thirdparty.ticketing.domain.zone.dto;

import java.util.List;

import jakarta.validation.Valid;

import lombok.Data;

@Data
public class ZoneCreationRequest {
    @Valid private List<ZoneCreationElement> zones;
}
