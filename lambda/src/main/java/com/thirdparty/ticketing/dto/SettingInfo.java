package com.thirdparty.ticketing.dto;

public record SettingInfo(
        String uri,
        int offset,
        int limit,
        long performanceId) {
}
