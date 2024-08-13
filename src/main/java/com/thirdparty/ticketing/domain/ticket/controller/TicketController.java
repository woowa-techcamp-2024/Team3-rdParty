package com.thirdparty.ticketing.domain.ticket.controller;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.thirdparty.ticketing.domain.ItemResult;
import com.thirdparty.ticketing.domain.ticket.dto.SeatSelectionRequest;
import com.thirdparty.ticketing.domain.ticket.dto.TicketElement;
import com.thirdparty.ticketing.domain.ticket.dto.TicketPaymentRequest;
import com.thirdparty.ticketing.domain.ticket.service.TicketService;
import com.thirdparty.ticketing.global.security.LoginMember;

import lombok.RequiredArgsConstructor;

@RestController("/api")
@RequiredArgsConstructor
public class TicketController {
    private final TicketService ticketService;

    @GetMapping("/members/tickets")
    public ResponseEntity<ItemResult<TicketElement>> selectMyTickets(@LoginMember String email) {
        return ResponseEntity.ok().body(ticketService.selectMyTicket(email));
    }

    @PostMapping("/seats/select")
    public ResponseEntity<Void> selectSeat(
            @RequestBody @Valid SeatSelectionRequest seatSelectionRequest) {
        ticketService.selectSeat(seatSelectionRequest);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/tickets")
    public ResponseEntity<Void> payTicket(
            @RequestBody @Valid TicketPaymentRequest ticketPaymentRequest) {
        ticketService.reservationTicket(ticketPaymentRequest);
        return ResponseEntity.ok().build();
    }
}
