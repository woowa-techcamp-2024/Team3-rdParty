package com.thirdparty.ticketing.waiting.event;

import org.springframework.context.event.EventListener;

import com.thirdparty.ticketing.waiting.waitingsystem.WaitingSystem;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class WaitingSystemEventListener {
    private final WaitingSystem waitingSystem;

    @EventListener(PollingEvent.class)
    public void moveUserToRunningRoom(PollingEvent event) {
        waitingSystem.moveUserToRunning(event.getPerformanceId());
    }
}
