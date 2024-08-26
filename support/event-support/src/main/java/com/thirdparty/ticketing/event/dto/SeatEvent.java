package com.thirdparty.ticketing.event.dto;

import com.thirdparty.ticketing.event.Event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SeatEvent implements Event {
    private final String memberEmail;
    private final Long seatId;
    private final EventType eventType;

    public enum EventType {
        SELECT,
        RELEASE
    }
}
