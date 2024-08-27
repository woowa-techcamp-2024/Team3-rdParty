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
import com.thirdparty.ticketing.domain.seat.RedisSeat;
import com.thirdparty.ticketing.domain.seat.Seat;
import com.thirdparty.ticketing.domain.seat.repository.LettuceSeatRepository;
import com.thirdparty.ticketing.domain.ticket.Ticket;
import com.thirdparty.ticketing.domain.ticket.dto.event.PaymentEvent;
import com.thirdparty.ticketing.domain.ticket.dto.event.SeatEvent;
import com.thirdparty.ticketing.domain.ticket.dto.request.SeatSelectionRequest;
import com.thirdparty.ticketing.domain.ticket.dto.request.TicketPaymentRequest;
import com.thirdparty.ticketing.domain.ticket.repository.TicketRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class ReservationRedisService implements ReservationService {

    private final MemberRepository memberRepository;
    private final EventPublisher eventPublisher;
    private final PaymentProcessor paymentProcessor;
    private final LettuceSeatRepository lettuceSeatRepository;
    private final ReservationManager reservationManager;
    private final TicketRepository ticketRepository;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(5);

    @Value("${ticketing.reservation.release-delay-seconds}")
    private int reservationReleaseDelay;

    @Override
    @Transactional
    public void selectSeat(String memberEmail, SeatSelectionRequest seatSelectionRequest) {
        Long seatId = seatSelectionRequest.getSeatId();

        RedisSeat seat =
                lettuceSeatRepository
                        .findBySeatId(seatId)
                        .orElseThrow(() -> new TicketingException(ErrorCode.NOT_FOUND_SEAT));
        seat.checkValid(); // 좌석이 선택 가능한지 확인 -> 디비 부하를 줄이기 위해 배치

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
    public void reservationTicket(String memberEmail, TicketPaymentRequest ticketPaymentRequest) {
        Long seatId = ticketPaymentRequest.getSeatId();
        RedisSeat seat =
                lettuceSeatRepository
                        .findById(seatId)
                        .orElseThrow(() -> new TicketingException(ErrorCode.NOT_FOUND_SEAT));

        Member loginMember =
                memberRepository
                        .findByEmail(memberEmail)
                        .orElseThrow(() -> new TicketingException(ErrorCode.NOT_FOUND_MEMBER));

        processPayment(seat, loginMember);

        Ticket ticket =
                Ticket.builder()
                        .ticketSerialNumber(UUID.randomUUID())
                        .seat(Seat.builder().seatId(seat.getSeatId()).build())
                        .member(loginMember)
                        .build();
        try {
            seat.markAsPaid();
            ticketRepository.save(ticket);
        } catch (Exception e) {
            log.error("Failed to save ticket: {}", e.getMessage());
            seat.markAsSelected();
            throw new TicketingException(ErrorCode.PAYMENT_FAILED);
        } finally {
            lettuceSeatRepository.save(seat);
        }
    }

    @Override
    public void releaseSeat(String memberEmail, SeatSelectionRequest seatSelectionRequest) {
        // TODO 구현하기 후순위
    }

    private void processPayment(RedisSeat seat, Member loginMember) {
        if (!seat.isAssignedByMember(loginMember)) {
            throw new TicketingException(ErrorCode.NOT_SELECTABLE_SEAT);
        }
        paymentProcessor.processPayment(new PaymentRequest());
        PaymentEvent paymentEvent = new PaymentEvent(loginMember.getEmail());
        eventPublisher.publish(paymentEvent);
    }
}
