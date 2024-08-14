package com.thirdparty.ticketing.domain.ticket.dto;

import java.util.UUID;

import com.thirdparty.ticketing.domain.ticket.Ticket;

import lombok.Data;

@Data
public class TicketElement {
    private final UUID serialNumber;

    public static TicketElement of(Ticket ticket) {
        return new TicketElement(ticket.getTicketSerialNumber());
    }
}
