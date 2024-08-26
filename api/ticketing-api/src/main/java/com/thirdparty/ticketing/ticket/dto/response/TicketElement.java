package com.thirdparty.ticketing.ticket.dto.response;

import java.util.UUID;

import com.thirdparty.ticketing.jpa.performance.Performance;
import com.thirdparty.ticketing.jpa.seat.Seat;
import com.thirdparty.ticketing.jpa.ticket.Ticket;
import com.thirdparty.ticketing.performance.dto.PerformanceElement;

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
