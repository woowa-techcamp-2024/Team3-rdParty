package com.thirdparty.ticketing.event.dto;

import com.thirdparty.ticketing.event.Event;

import lombok.Data;

@Data
public class PaymentEvent implements Event {
    private final String email;
}
