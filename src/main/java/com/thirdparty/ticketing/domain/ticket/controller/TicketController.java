package com.thirdparty.ticketing.domain.ticket.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.thirdparty.ticketing.domain.ItemResult;
import com.thirdparty.ticketing.domain.common.LoginMember;
import com.thirdparty.ticketing.domain.ticket.dto.SeatSelectionRequest;
import com.thirdparty.ticketing.domain.ticket.dto.TicketElement;
import com.thirdparty.ticketing.domain.ticket.dto.TicketPaymentRequest;
import com.thirdparty.ticketing.domain.ticket.service.ReservationServiceProxy;
import com.thirdparty.ticketing.domain.ticket.service.TicketService;

@RestController
@RequestMapping("/api")
public class TicketController {
    private final TicketService ticketService;
    private final ReservationServiceProxy reservationServiceProxy;

    public TicketController(
            TicketService ticketService,
            @Qualifier("reddisonReservationServiceProxy")
                    ReservationServiceProxy reservationServiceProxy) {
        this.ticketService = ticketService;
        this.reservationServiceProxy = reservationServiceProxy;
    }

    @GetMapping("/members/tickets")
    public ResponseEntity<ItemResult<TicketElement>> selectMyTickets(
            @LoginMember String memberEmail) {
        ItemResult<TicketElement> tickets = ItemResult.of(List.of());
        ticketService.selectMyTicket(memberEmail);
        return ResponseEntity.ok().body(tickets);
    }

    @PostMapping("/seats/select")
    public ResponseEntity<Void> selectSeat(
            @LoginMember String memberEmail,
            @RequestBody @Valid SeatSelectionRequest seatSelectionRequest) {
        reservationServiceProxy.selectSeat(memberEmail, seatSelectionRequest);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/tickets")
    public ResponseEntity<Void> reservationTicket(
            @LoginMember String memberEmail,
            @RequestBody @Valid TicketPaymentRequest ticketPaymentRequest) {
        reservationServiceProxy.reservationTicket(memberEmail, ticketPaymentRequest);
        return ResponseEntity.ok().build();
    }
}
