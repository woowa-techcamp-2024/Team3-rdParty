package com.thirdparty.ticketing.domain.waitingroom;

import lombok.Data;

@Data
public class WaitingMember {
    private final String email;
    private final String performanceId;
}
