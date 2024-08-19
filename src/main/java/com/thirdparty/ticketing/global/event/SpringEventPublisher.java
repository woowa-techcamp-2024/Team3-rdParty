package com.thirdparty.ticketing.global.event;

import org.springframework.context.ApplicationEventPublisher;

import com.thirdparty.ticketing.domain.common.Event;
import com.thirdparty.ticketing.domain.common.EventPublisher;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SpringEventPublisher implements EventPublisher {

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void publish(Event event) {
        eventPublisher.publishEvent(event);
    }
}
