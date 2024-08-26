package com.thirdparty.ticketing.event.config;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.thirdparty.ticketing.event.EventPublisher;
import com.thirdparty.ticketing.event.SpringEventPublisher;

@Configuration
public class EventConfig {

    @Bean
    public EventPublisher eventPublisher(ApplicationEventPublisher eventPublisher) {
        return new SpringEventPublisher(eventPublisher);
    }

    //    @Bean
    //    public WaitingEventListener waitingEventListener(WaitingSystem waitingSystem) {
    //        return new WaitingEventListener(waitingSystem);
    //    }
}
