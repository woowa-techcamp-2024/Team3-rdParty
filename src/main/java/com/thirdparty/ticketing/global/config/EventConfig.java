package com.thirdparty.ticketing.global.config;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.thirdparty.ticketing.domain.common.EventPublisher;
import com.thirdparty.ticketing.domain.waitingsystem.WaitingSystem;
import com.thirdparty.ticketing.global.event.SpringEventPublisher;
import com.thirdparty.ticketing.global.waitingsystem.WaitingEventListener;

@Configuration
public class EventConfig {

    @Bean
    public EventPublisher eventPublisher(ApplicationEventPublisher eventPublisher) {
        return new SpringEventPublisher(eventPublisher);
    }

    @Bean
    public WaitingEventListener waitingEventListener(WaitingSystem waitingSystem) {
        return new WaitingEventListener(waitingSystem);
    }
}
