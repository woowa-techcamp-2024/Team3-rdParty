package com.thirdparty.ticketing.domain.common;

public class TicketingException extends RuntimeException {

    public TicketingException(String message) {
        super(message);
    }

    public TicketingException(String message, Throwable cause) {
        super(message, cause);
    }
}
