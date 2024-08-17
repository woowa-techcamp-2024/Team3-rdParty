package com.thirdparty.ticketing.domain.ticket.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.thirdparty.ticketing.domain.ItemResult;
import com.thirdparty.ticketing.domain.common.LoginMember;
import com.thirdparty.ticketing.domain.ticket.dto.SeatSelectionRequest;
import com.thirdparty.ticketing.domain.ticket.dto.TicketElement;
import com.thirdparty.ticketing.domain.ticket.dto.TicketPaymentRequest;
import com.thirdparty.ticketing.domain.ticket.service.ReservationService;
import com.thirdparty.ticketing.domain.ticket.service.TicketService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TicketController {
    private final TicketService ticketService;
    private final ReservationService reservationService;

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
        reservationService.selectSeat(memberEmail, seatSelectionRequest);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/tickets")
    public ResponseEntity<Void> reservationTicket(
            @LoginMember String memberEmail,
            @RequestBody @Valid TicketPaymentRequest ticketPaymentRequest) {
        reservationService.reservationTicket(memberEmail, ticketPaymentRequest);
        return ResponseEntity.ok().build();
    }
}
