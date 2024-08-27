package com.thirdparty.ticketing.domain.ticket.dto.event;

import com.thirdparty.ticketing.domain.common.Event;

import lombok.Data;

@Data
public class PaymentEvent implements Event {
    private final String email;
}
