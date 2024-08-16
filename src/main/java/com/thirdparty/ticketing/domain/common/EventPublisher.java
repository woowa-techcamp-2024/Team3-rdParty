package com.thirdparty.ticketing.domain.common;

public interface EventPublisher {

	void publish(Event event);
}
