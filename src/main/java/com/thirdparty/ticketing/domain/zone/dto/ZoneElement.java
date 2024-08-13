package com.thirdparty.ticketing.domain.zone.dto;

import com.thirdparty.ticketing.domain.zone.Zone;

import lombok.Data;

@Data
public class ZoneElement {

	private final Long zoneId;

	private final String zoneName;

	public static ZoneElement of(Zone zone) {
		return new ZoneElement(zone.getZoneId(), zone.getZoneName());
	}
}
