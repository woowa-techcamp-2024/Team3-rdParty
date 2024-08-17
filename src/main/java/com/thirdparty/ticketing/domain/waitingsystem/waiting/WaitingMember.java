package com.thirdparty.ticketing.domain.waitingsystem.waiting;

import java.time.ZonedDateTime;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class WaitingMember {
    private String email;
    private long performanceId;
    private long waitingCount;
    private ZonedDateTime enteredAt;

    public WaitingMember(String email, String performanceId) {
        this.email = email;
        this.performanceId = Long.parseLong(performanceId);
    }

    public WaitingMember(String email, Long performanceId) {
        this.email = email;
        this.performanceId = performanceId;
    }

    public void updateWaitingInfo(long waitingCount, ZonedDateTime enteredAt) {
        this.waitingCount = waitingCount;
        this.enteredAt = enteredAt;
    }
}
