package com.thirdparty.ticketing.domain.waitingsystem;

import com.thirdparty.ticketing.domain.common.Event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class LastPollingEvent implements Event {

    private final String email;
    private final long performanceId;
}
