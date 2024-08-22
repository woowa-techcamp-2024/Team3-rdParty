package com.thirdparty.ticketing.domain.ticket.service;

import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import com.thirdparty.ticketing.domain.common.ErrorCode;
import com.thirdparty.ticketing.domain.common.EventPublisher;
import com.thirdparty.ticketing.domain.common.TicketingException;
import com.thirdparty.ticketing.domain.member.Member;
import com.thirdparty.ticketing.domain.member.repository.MemberRepository;
import com.thirdparty.ticketing.domain.payment.PaymentProcessor;
import com.thirdparty.ticketing.domain.payment.dto.PaymentRequest;
import com.thirdparty.ticketing.domain.seat.Seat;
import com.thirdparty.ticketing.domain.ticket.Ticket;
import com.thirdparty.ticketing.domain.ticket.dto.event.PaymentEvent;
import com.thirdparty.ticketing.domain.ticket.dto.request.SeatSelectionRequest;
import com.thirdparty.ticketing.domain.ticket.dto.request.TicketPaymentRequest;
import com.thirdparty.ticketing.domain.ticket.repository.TicketRepository;
import com.thirdparty.ticketing.domain.ticket.service.strategy.LockSeatStrategy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class ReservationTransactionService implements ReservationService {
    private final TicketRepository ticketRepository;
    private final MemberRepository memberRepository;
    private final PaymentProcessor paymentProcessor;
    private final LockSeatStrategy lockSeatStrategy;
    private final EventPublisher eventPublisher;

    private final ReservationManager reservationManager;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(5);

    @Value("ticketing.reservation.release-delay-seconds")
    private int reservationReleaseDelay;

    @Override
    @Transactional
    public void selectSeat(String memberEmail, SeatSelectionRequest seatSelectionRequest) {
        Long seatId = seatSelectionRequest.getSeatId();

        Seat seat =
                lockSeatStrategy
                        .getSeatWithLock(seatId)
                        .orElseThrow(() -> new TicketingException(ErrorCode.NOT_FOUND_SEAT));

        Member member =
                memberRepository
                        .findByEmail(memberEmail)
                        .orElseThrow(() -> new TicketingException(ErrorCode.NOT_FOUND_MEMBER));

        seat.assignByMember(member);
        scheduler.schedule(
                () -> reservationManager.releaseSeat(member, seatId),
                reservationReleaseDelay,
                TimeUnit.SECONDS);
    }

    @Override
    @Transactional
    public void releaseSeat(String memberEmail, SeatSelectionRequest seatSelectionRequest) {
        Member member =
                memberRepository
                        .findByEmail(memberEmail)
                        .orElseThrow(() -> new TicketingException(ErrorCode.NOT_FOUND_MEMBER));

        reservationManager.releaseSeat(member, seatSelectionRequest.getSeatId());
    }

    @Override
    @Transactional
    public void reservationTicket(String memberEmail, TicketPaymentRequest ticketPaymentRequest) {
        Long seatId = ticketPaymentRequest.getSeatId();
        Seat seat =
                lockSeatStrategy
                        .getSeatWithLock(seatId)
                        .orElseThrow(() -> new TicketingException(ErrorCode.NOT_FOUND_SEAT));

        Member loginMember =
                memberRepository
                        .findByEmail(memberEmail)
                        .orElseThrow(() -> new TicketingException(ErrorCode.NOT_FOUND_MEMBER));

        processPayment(seat, loginMember);

        Ticket ticket =
                Ticket.builder()
                        .ticketSerialNumber(UUID.randomUUID())
                        .seat(seat)
                        .member(loginMember)
                        .build();

        ticketRepository.save(ticket);
    }

    private void processPayment(Seat seat, Member loginMember) {
        if (!seat.isAssignedByMember(loginMember)) {
            throw new TicketingException(ErrorCode.NOT_SELECTABLE_SEAT);
        }

        seat.markAsPendingPayment();
        paymentProcessor.processPayment(new PaymentRequest());
        seat.markAsPaid();
        PaymentEvent paymentEvent = new PaymentEvent(loginMember.getEmail());
        eventPublisher.publish(paymentEvent);
    }
}
