package com.thirdparty.ticketing.global.waitingsystem;

import com.thirdparty.ticketing.domain.waitingsystem.LastPollingEvent;
import java.util.Optional;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.context.event.EventListener;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.thirdparty.ticketing.domain.common.ErrorCode;
import com.thirdparty.ticketing.domain.common.TicketingException;
import com.thirdparty.ticketing.domain.ticket.dto.event.PaymentEvent;
import com.thirdparty.ticketing.domain.waitingsystem.PollingEvent;
import com.thirdparty.ticketing.domain.waitingsystem.WaitingSystem;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class WaitingEventListener {

    private final WaitingSystem waitingSystem;

    @EventListener(PollingEvent.class)
    public void moveUserToRunningRoom(PollingEvent event) {
        waitingSystem.moveUserToRunning(event.getPerformanceId());
    }

    @EventListener(LastPollingEvent.class)
    public void updateRunningMemberExpiredTime(LastPollingEvent event) {
        waitingSystem.updateRunningMemberExpiredTime(event.getEmail(), event.getPerformanceId());
    }

    @TransactionalEventListener(PaymentEvent.class)
    public void pullOutRunningMember(PaymentEvent event) {
        HttpServletRequest request =
                ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                        .getRequest();
        long performanceId =
                Optional.ofNullable(request.getHeader("performanceId"))
                        .map(Long::parseLong)
                        .orElseThrow(
                                () ->
                                        new TicketingException(
                                                ErrorCode.NOT_CONTAINS_PERFORMANCE_INFO));
        waitingSystem.pullOutRunningMember(event.getEmail(), performanceId);
    }
}
