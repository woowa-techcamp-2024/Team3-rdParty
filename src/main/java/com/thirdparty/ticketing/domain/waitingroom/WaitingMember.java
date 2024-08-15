package com.thirdparty.ticketing.domain.waitingroom;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.ZonedDateTime;

@Data
@RequiredArgsConstructor
public class WaitingMember {
    private final String email;
    private final Long performanceId;
    private long waitingCounter;
    private ZonedDateTime enteredAt;
}
