package com.thirdparty.ticketing.domain.ticket.controller;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thirdparty.ticketing.domain.ItemResult;
import com.thirdparty.ticketing.domain.common.LoginMember;
import com.thirdparty.ticketing.domain.ticket.dto.request.SeatSelectionRequest;
import com.thirdparty.ticketing.domain.ticket.dto.request.TicketPaymentRequest;
import com.thirdparty.ticketing.domain.ticket.dto.response.TicketElement;
import com.thirdparty.ticketing.domain.ticket.service.ReservationService;
import com.thirdparty.ticketing.domain.ticket.service.TicketService;
import com.thirdparty.ticketing.domain.waitingsystem.Waiting;

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
        ItemResult<TicketElement> tickets = ticketService.selectMyTicket(memberEmail);
        return ResponseEntity.ok().body(tickets);
    }

    @PostMapping("/seats/release")
    public ResponseEntity<Void> releaseSeat(
            @LoginMember String memberEmail,
            @RequestBody @Valid SeatSelectionRequest seatSelectionRequest) {
        reservationService.releaseSeat(memberEmail, seatSelectionRequest);
        return ResponseEntity.ok().build();
    }

    @Waiting
    @PostMapping("/seats/select")
    public ResponseEntity<Void> selectSeat(
            @LoginMember String memberEmail,
            @RequestBody @Valid SeatSelectionRequest seatSelectionRequest) {
        reservationService.selectSeat(memberEmail, seatSelectionRequest);
        return ResponseEntity.ok().build();
    }

    @Waiting
    @PostMapping("/tickets")
    public ResponseEntity<Void> reservationTicket(
            @LoginMember String memberEmail,
            @RequestBody @Valid TicketPaymentRequest ticketPaymentRequest) {
        reservationService.reservationTicket(memberEmail, ticketPaymentRequest);
        return ResponseEntity.ok().build();
    }
}
