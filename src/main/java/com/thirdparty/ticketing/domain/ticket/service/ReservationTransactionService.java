package com.thirdparty.ticketing.domain.ticket.service;

import jakarta.persistence.OptimisticLockException;

import org.springframework.transaction.annotation.Transactional;

import com.thirdparty.ticketing.domain.common.ErrorCode;
import com.thirdparty.ticketing.domain.common.TicketingException;
import com.thirdparty.ticketing.domain.member.Member;
import com.thirdparty.ticketing.domain.member.repository.MemberRepository;
import com.thirdparty.ticketing.domain.payment.PaymentProcessor;
import com.thirdparty.ticketing.domain.payment.dto.PaymentRequest;
import com.thirdparty.ticketing.domain.seat.Seat;
import com.thirdparty.ticketing.domain.ticket.dto.SeatSelectionRequest;
import com.thirdparty.ticketing.domain.ticket.dto.TicketPaymentRequest;
import com.thirdparty.ticketing.domain.ticket.service.strategy.LockSeatStrategy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class ReservationTransactionService implements ReservationService {
    private final MemberRepository memberRepository;
    private final PaymentProcessor paymentProcessor;
    private final LockSeatStrategy lockSeatStrategy;

    @Override
    @Transactional
    public void selectSeat(String memberEmail, SeatSelectionRequest seatSelectionRequest) {
        try {
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
        } catch (OptimisticLockException e) {
            throw new TicketingException(ErrorCode.NOT_SELECTABLE_SEAT);
        }
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

        seat.markAsPendingPayment();
        paymentProcessor.processPayment(new PaymentRequest());
        seat.markAsPaid();

        if (seat.isAssignedByMember(loginMember)) {
            throw new TicketingException(ErrorCode.NOT_SELECTABLE_SEAT);
        }
    }
}
