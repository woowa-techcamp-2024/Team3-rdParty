package com.thirdparty.ticketing.domain.ticket.dto.event;

import com.thirdparty.ticketing.domain.common.Event;

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
