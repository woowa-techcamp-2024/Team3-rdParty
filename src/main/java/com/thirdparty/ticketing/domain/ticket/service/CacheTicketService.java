package com.thirdparty.ticketing.domain.ticket.service;

import java.util.NoSuchElementException;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.thirdparty.ticketing.domain.common.ErrorCode;
import com.thirdparty.ticketing.domain.common.TicketingException;
import com.thirdparty.ticketing.domain.member.Member;
import com.thirdparty.ticketing.domain.member.repository.MemberRepository;
import com.thirdparty.ticketing.domain.payment.PaymentProcessor;
import com.thirdparty.ticketing.domain.seat.Seat;
import com.thirdparty.ticketing.domain.seat.SeatStatus;
import com.thirdparty.ticketing.domain.seat.repository.SeatRepository;
import com.thirdparty.ticketing.domain.ticket.dto.SeatSelectionRequest;
import com.thirdparty.ticketing.domain.ticket.dto.TicketPaymentRequest;
import com.thirdparty.ticketing.domain.ticket.repository.TicketRepository;

@Service
public class CacheTicketService extends TicketService {

    public CacheTicketService(
            MemberRepository memberRepository,
            TicketRepository ticketRepository,
            SeatRepository seatRepository,
            PaymentProcessor paymentProcessor) {
        super(memberRepository, ticketRepository, seatRepository, paymentProcessor);
    }

    @Override
    @Transactional
    public void selectSeat(String memberEmail, SeatSelectionRequest seatSelectionRequest) {
        Member member =
                memberRepository
                        .findByEmail(memberEmail)
                        .orElseThrow(() -> new TicketingException(ErrorCode.NOT_FOUND_MEMBER));

        // TODO: 에러 땜빵한거 고치기
        Seat seat =
                seatRepository
                        .findById(seatSelectionRequest.getSeatId())
                        .orElseThrow(() -> new TicketingException(ErrorCode.NOT_FOUND_MEMBER));

        if (!seat.empty()) {
            throw new RuntimeException("자리에 주인이 있습니다.");
        }

        seat.designateMember(member);
    }

    @Override
    public void reservationTicket(String memberEmail, TicketPaymentRequest ticketPaymentRequest) {
        long temp = 1L;
        Long seatId = ticketPaymentRequest.getSeatId();
        Seat seat = seatRepository.findById(seatId)
                .orElseThrow(NoSuchElementException::new);

        Member loginMember =
                memberRepository
                        .findByEmail(memberEmail)
                        .orElseThrow(() -> new TicketingException(ErrorCode.NOT_FOUND_MEMBER));

        if (!seat.getSeatStatus().equals(SeatStatus.SELECTED)) {
            //TODO: 상태 변경
            temp+=1;
        }
        // paymentProcessor.processPayment();
        if (!seat.getSeatStatus().equals(SeatStatus.PAID)) {
            //TODO: 상태 변경
            // seat.updateStatus(SeatStatus.PAID);
            temp+=1;
        }

        if (!seat.getMember().getMemberId().equals(loginMember.getMemberId())) {
            // TODO: 에러 변경
            throw new TicketingException(ErrorCode.NOT_FOUND_ZONE);
        }
    }
}
