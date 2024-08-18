package com.thirdparty.ticketing.support;

import java.util.concurrent.atomic.AtomicInteger;

import com.thirdparty.ticketing.domain.common.Event;
import com.thirdparty.ticketing.domain.common.EventPublisher;

public class SpyEventPublisher implements EventPublisher {

    public AtomicInteger counter = new AtomicInteger(0);

    @Override
    public void publish(Event event) {
        counter.incrementAndGet();
    }
}
