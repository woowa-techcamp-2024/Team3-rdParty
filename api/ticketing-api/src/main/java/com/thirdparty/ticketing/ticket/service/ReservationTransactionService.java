package com.thirdparty.ticketing.ticket.service;

import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import com.thirdparty.ticketing.event.EventPublisher;
import com.thirdparty.ticketing.event.dto.PaymentEvent;
import com.thirdparty.ticketing.event.dto.SeatEvent;
import com.thirdparty.ticketing.exception.ErrorCode;
import com.thirdparty.ticketing.exception.TicketingException;
import com.thirdparty.ticketing.jpa.member.Member;
import com.thirdparty.ticketing.jpa.member.repository.MemberRepository;
import com.thirdparty.ticketing.jpa.payment.PaymentProcessor;
import com.thirdparty.ticketing.jpa.payment.dto.PaymentRequest;
import com.thirdparty.ticketing.jpa.seat.Seat;
import com.thirdparty.ticketing.jpa.ticket.Ticket;
import com.thirdparty.ticketing.jpa.ticket.repository.TicketRepository;
import com.thirdparty.ticketing.ticket.dto.request.SeatSelectionRequest;
import com.thirdparty.ticketing.ticket.dto.request.TicketPaymentRequest;
import com.thirdparty.ticketing.ticket.service.strategy.LockSeatStrategy;

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

    @Value("${ticketing.reservation.release-delay-seconds}")
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

        // 좌석 선택 이벤트 발행
        eventPublisher.publish(new SeatEvent(memberEmail, seatId, SeatEvent.EventType.SELECT));

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

        Long seatId = seatSelectionRequest.getSeatId();
        reservationManager.releaseSeat(member, seatId);

        // 좌석 해제 이벤트 발행
        eventPublisher.publish(new SeatEvent(memberEmail, seatId, SeatEvent.EventType.RELEASE));
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
