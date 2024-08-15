package com.thirdparty.ticketing.domain.waiting;

import java.time.ZonedDateTime;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class WaitingMember {
    private final String email;
    private final Long performanceId;
    private long waitingCounter;
    private ZonedDateTime enteredAt;
}
