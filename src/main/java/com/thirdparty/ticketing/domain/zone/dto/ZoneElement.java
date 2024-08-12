package com.thirdparty.ticketing.domain.zone.dto;

import com.thirdparty.ticketing.domain.zone.Zone;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ZoneElement {

    @NotBlank(message = "구역 아이디는 공백일 수 없습니다.")
    private Long zoneId;

    @NotBlank(message = "구역이름은 공백일 수 없습니다.")
    private String zoneName;

    public static ZoneElement of(Zone zone) {
        ZoneElement zoneElement = new ZoneElement();
        zoneElement.setZoneId(zone.getZoneId());
        zoneElement.setZoneName(zone.getZoneName());
        return zoneElement;
    }
}
