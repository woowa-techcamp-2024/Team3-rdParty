package com.thirdparty.ticketing.domain.ticket.service;

import jakarta.persistence.OptimisticLockException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thirdparty.ticketing.domain.common.ErrorCode;
import com.thirdparty.ticketing.domain.common.TicketingException;
import com.thirdparty.ticketing.domain.member.Member;
import com.thirdparty.ticketing.domain.member.repository.MemberRepository;
import com.thirdparty.ticketing.domain.payment.PaymentProcessor;
import com.thirdparty.ticketing.domain.payment.dto.PaymentRequest;
import com.thirdparty.ticketing.domain.seat.Seat;
import com.thirdparty.ticketing.domain.seat.SeatStatus;
import com.thirdparty.ticketing.domain.seat.repository.SeatRepository;
import com.thirdparty.ticketing.domain.ticket.dto.SeatSelectionRequest;
import com.thirdparty.ticketing.domain.ticket.dto.TicketPaymentRequest;
import com.thirdparty.ticketing.domain.ticket.policy.LockSeatStrategy;
import com.thirdparty.ticketing.domain.ticket.repository.TicketRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PersistenceTicketService extends TicketService {
    private final LockSeatStrategy lockSeatStrategy;

    public PersistenceTicketService(
            MemberRepository memberRepository,
            TicketRepository ticketRepository,
            SeatRepository seatRepository,
            PaymentProcessor paymentProcessor,
            LockSeatStrategy lockSeatStrategy) {
        super(memberRepository, ticketRepository, seatRepository, paymentProcessor);
        this.lockSeatStrategy = lockSeatStrategy;
    }

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

        if (!seat.getSeatStatus().equals(SeatStatus.SELECTED)) {
            seat.updateStatus(SeatStatus.PENDING_PAYMENT);
        }
        paymentProcessor.processPayment(new PaymentRequest());
        if (!seat.getSeatStatus().equals(SeatStatus.PAID)) {
            seat.updateStatus(SeatStatus.PAID);
        }

        if (!seat.getMember().getMemberId().equals(loginMember.getMemberId())) {
            throw new TicketingException(ErrorCode.NOT_SELECTABLE_SEAT);
        }
    }
}
