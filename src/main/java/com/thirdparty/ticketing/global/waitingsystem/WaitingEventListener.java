package com.thirdparty.ticketing.global.waitingsystem;

import com.thirdparty.ticketing.domain.waitingsystem.PollingEvent;
import com.thirdparty.ticketing.domain.waitingsystem.WaitingSystem;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;

@RequiredArgsConstructor
public class WaitingEventListener {

    private final WaitingSystem waitingSystem;

    @EventListener(PollingEvent.class)
    public void moveUserToRunningRoom(PollingEvent event) {
        waitingSystem.moveUserToRunning(event.getPerformanceId());
    }
}
