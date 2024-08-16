package com.thirdparty.ticketing.domain.ticket.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.thirdparty.ticketing.domain.ItemResult;
import com.thirdparty.ticketing.domain.common.LoginMember;
import com.thirdparty.ticketing.domain.ticket.dto.SeatSelectionRequest;
import com.thirdparty.ticketing.domain.ticket.dto.TicketElement;
import com.thirdparty.ticketing.domain.ticket.dto.TicketPaymentRequest;

@RestController("/api")
public class TicketController {

    @GetMapping("/members/tickets")
    public ResponseEntity<ItemResult<TicketElement>> selectMyTickets(
            @LoginMember String memberEmail) {
        ItemResult<TicketElement> tickets = ItemResult.of(List.of());
        // ticketService.selectMyTicket(memberEmail);
        return ResponseEntity.ok().body(tickets);
    }

    @PostMapping("/seats/select")
    public ResponseEntity<Void> selectSeat(
            @LoginMember String memberEmail,
            @RequestBody @Valid SeatSelectionRequest seatSelectionRequest) {
        return ResponseEntity.ok().build();
    }

    @PostMapping("/tickets")
    public ResponseEntity<Void> payTicket(
            @LoginMember String memberEmail,
            @RequestBody @Valid TicketPaymentRequest ticketPaymentRequest) {
        return ResponseEntity.ok().build();
    }
}
