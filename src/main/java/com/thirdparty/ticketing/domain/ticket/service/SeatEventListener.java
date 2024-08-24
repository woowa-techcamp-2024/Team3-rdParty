package com.thirdparty.ticketing.domain.ticket.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.thirdparty.ticketing.domain.ticket.controller.TicketSseController;
import com.thirdparty.ticketing.domain.ticket.dto.event.SeatEvent;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SeatEventListener {
    private final TicketSseController ticketSseController;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleSeatEvent(SeatEvent event) {
        ticketSseController.sendEventToPerformance(event);
    }
}
