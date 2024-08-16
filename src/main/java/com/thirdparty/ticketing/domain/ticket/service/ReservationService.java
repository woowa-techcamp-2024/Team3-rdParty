package com.thirdparty.ticketing.domain.ticket.service;

import com.thirdparty.ticketing.domain.ticket.dto.SeatSelectionRequest;
import com.thirdparty.ticketing.domain.ticket.dto.TicketPaymentRequest;

public interface ReservationService {
    void selectSeat(String memberEmail, SeatSelectionRequest seatSelectionRequest);

    void reservationTicket(String memberEmail, TicketPaymentRequest ticketPaymentRequest);
}
