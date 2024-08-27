package com.thirdparty.ticketing.domain.ticket.service;

import java.util.UUID;

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
import com.thirdparty.ticketing.domain.seat.repository.SeatRepository;
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
    private final SeatRepository seatRepository;
    private final TicketRepository ticketRepository;

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
        eventPublisher.publish(new SeatEvent(memberEmail, seatId, SeatEvent.EventType.SELECT));
    }

    @Override
    @Transactional
    public void reservationTicket(String memberEmail, TicketPaymentRequest ticketPaymentRequest) {
        Long seatId = ticketPaymentRequest.getSeatId();
        RedisSeat redisSeat =
                lettuceSeatRepository
                        .findById(seatId)
                        .orElseThrow(() -> new TicketingException(ErrorCode.NOT_FOUND_SEAT));

        Seat seat =
                seatRepository
                        .findById(seatId)
                        .orElseThrow(() -> new TicketingException(ErrorCode.NOT_FOUND_SEAT));

        Member loginMember =
                memberRepository
                        .findByEmail(memberEmail)
                        .orElseThrow(() -> new TicketingException(ErrorCode.NOT_FOUND_MEMBER));

        processPayment(redisSeat, loginMember);

        Ticket ticket =
                Ticket.builder()
                        .ticketSerialNumber(UUID.randomUUID())
                        .seat(Seat.builder().seatId(redisSeat.getSeatId()).build())
                        .member(loginMember)
                        .build();
        try {
            // 이벤트 기반으로 하는게 제일 이상적이지만... 일단은 이렇게 -> 강제로 정합성을 통일함.
            redisSeat.markAsPaid();
            seat.assignByMember(loginMember);
            seat.markAsPaid();
            ticketRepository.save(ticket);
            seatRepository.save(seat);
        } catch (Exception e) {
            log.error("Failed to save ticket: {}", e.getMessage());
            redisSeat.markAsSelected();
            throw new TicketingException(ErrorCode.PAYMENT_FAILED);
        } finally {
            lettuceSeatRepository.save(redisSeat);
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
