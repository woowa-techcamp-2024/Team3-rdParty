package com.thirdparty.ticketing.domain.ticket.service;

import com.thirdparty.ticketing.domain.ticket.dto.request.SeatSelectionRequest;
import com.thirdparty.ticketing.domain.ticket.dto.request.TicketPaymentRequest;

public interface ReservationService {
    void selectSeat(String memberEmail, SeatSelectionRequest seatSelectionRequest);

    void reservationTicket(String memberEmail, TicketPaymentRequest ticketPaymentRequest);

    void releaseSeat(String memberEmail, SeatSelectionRequest seatSelectionRequest);
}
