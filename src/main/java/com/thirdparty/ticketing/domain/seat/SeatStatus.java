package com.thirdparty.ticketing.domain.seat;

public enum SeatStatus {
    SELECTABLE,
    SELECTED,
    PENDING_PAYMENT,
    PAID;

    public boolean isSelectable() {
        return this == SELECTABLE;
    }
}
