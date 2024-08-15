package com.thirdparty.ticketing.domain.waitingsystem;

import com.thirdparty.ticketing.domain.common.Event;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PollingEvent implements Event {

    private final long performanceId;
}