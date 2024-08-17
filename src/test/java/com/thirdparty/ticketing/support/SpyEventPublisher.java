package com.thirdparty.ticketing.support;

import com.thirdparty.ticketing.domain.common.Event;
import com.thirdparty.ticketing.domain.common.EventPublisher;
import java.util.concurrent.atomic.AtomicInteger;

public class SpyEventPublisher implements EventPublisher {

    public AtomicInteger counter = new AtomicInteger(0);

    @Override
    public void publish(Event event) {
        counter.incrementAndGet();
    }
}
