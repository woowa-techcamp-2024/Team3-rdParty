package com.thirdparty.ticketing.ticket.event;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.thirdparty.ticketing.event.dto.SeatEvent;
import com.thirdparty.ticketing.ticket.controller.TicketSseController;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SeatEventListener {
    private final TicketSseController ticketSseController;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleSeatEvent(SeatEvent event) {
        ticketSseController.sendEventToPerformance(event);
    }
}
