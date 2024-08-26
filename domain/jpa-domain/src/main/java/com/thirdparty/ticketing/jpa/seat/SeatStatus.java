package com.thirdparty.ticketing.jpa.seat;

public enum SeatStatus {
    SELECTABLE,
    SELECTED,
    PENDING_PAYMENT,
    PAID;

    public boolean isSelectable() {
        return this == SELECTABLE;
    }

    public boolean isSelected() {
        return this == SELECTED;
    }

    public boolean isPendingPayment() {
        return this == PENDING_PAYMENT;
    }

    public boolean isPaid() {
        return this == PAID;
    }
}
