package com.thirdparty.ticketing.domain.seat;

public enum SeatStatus {
    AVAILABLE,
    SELECTED,
    PENDING_PAYMENT,
    PAID;

    public boolean isAvailable() {
        return this == AVAILABLE;
    }
}
