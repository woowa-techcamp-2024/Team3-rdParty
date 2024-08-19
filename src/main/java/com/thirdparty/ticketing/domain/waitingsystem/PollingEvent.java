package com.thirdparty.ticketing.domain.waitingsystem;

import com.thirdparty.ticketing.domain.common.Event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PollingEvent implements Event {

    private final long performanceId;
}
