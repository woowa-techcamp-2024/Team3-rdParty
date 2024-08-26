package com.thirdparty.ticketing.event.test;

import java.util.concurrent.atomic.AtomicInteger;

import com.thirdparty.ticketing.event.Event;
import com.thirdparty.ticketing.event.EventPublisher;

public class SpyEventPublisher implements EventPublisher {

    public AtomicInteger counter = new AtomicInteger(0);

    @Override
    public void publish(Event event) {
        counter.incrementAndGet();
    }
}
