package com.thirdparty.ticketing.waiting.event;

import com.thirdparty.ticketing.event.Event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PollingEvent implements Event {
    private final long performanceId;
}
