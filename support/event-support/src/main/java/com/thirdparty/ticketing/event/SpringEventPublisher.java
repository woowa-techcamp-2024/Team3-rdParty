package com.thirdparty.ticketing.event;

import org.springframework.context.ApplicationEventPublisher;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SpringEventPublisher implements EventPublisher {

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void publish(Event event) {
        eventPublisher.publishEvent(event);
    }
}
