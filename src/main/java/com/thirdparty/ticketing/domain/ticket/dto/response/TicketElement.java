package com.thirdparty.ticketing.domain.ticket.dto.response;

import java.util.UUID;

import com.thirdparty.ticketing.domain.performance.Performance;
import com.thirdparty.ticketing.domain.performance.dto.PerformanceElement;
import com.thirdparty.ticketing.domain.seat.Seat;
import com.thirdparty.ticketing.domain.ticket.Ticket;

import lombok.Data;

@Data
public class TicketElement {
    private final UUID serialNumber;
    private final PerformanceElement performance;
    private final TicketSeatDetail seat;

    public static TicketElement of(Ticket ticket) {
        Seat seat = ticket.getSeat();
        Performance performance = seat.getZone().getPerformance();

        return new TicketElement(
                ticket.getTicketSerialNumber(),
                PerformanceElement.of(performance),
                TicketSeatDetail.of(seat));
    }
}
