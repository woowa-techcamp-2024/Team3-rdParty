package com.thirdparty.ticketing.domain.ticket.service;

import jakarta.persistence.OptimisticLockException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.thirdparty.ticketing.domain.common.ErrorCode;
import com.thirdparty.ticketing.domain.common.TicketingException;
import com.thirdparty.ticketing.domain.member.Member;
import com.thirdparty.ticketing.domain.member.repository.MemberRepository;
import com.thirdparty.ticketing.domain.seat.Seat;
import com.thirdparty.ticketing.domain.payment.PaymentProcessor;
import com.thirdparty.ticketing.domain.seat.repository.SeatRepository;
import com.thirdparty.ticketing.domain.ticket.dto.SeatSelectionRequest;
import com.thirdparty.ticketing.domain.ticket.dto.TicketPaymentRequest;
import com.thirdparty.ticketing.domain.ticket.repository.TicketRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.logging.Logger;

@Service
public class PersistenceTicketService extends TicketService {
    private static final Logger log = LoggerFactory.getLogger(PersistenceTicketService.class);

    public PersistenceTicketService(
            MemberRepository memberRepository,
            TicketRepository ticketRepository,
            SeatRepository seatRepository,
            PaymentProcessor paymentProcessor) {
        super(memberRepository, ticketRepository, seatRepository, paymentProcessor);
    }

    @Override
    @Transactional
    public void selectSeat(SeatSelectionRequest seatSelectionRequest) {
        try {
            Long seatId = seatSelectionRequest.getSeatId();
            Optional<Seat> byId = seatRepository.findById(seatId);
            Seat seat = byId.orElseThrow(() -> new TicketingException(ErrorCode.NOT_FOUND_SEAT));

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = (String) authentication.getPrincipal();

            Member member =
                    memberRepository
                            .findByEmail(email)
                            .orElseThrow(() -> new TicketingException(ErrorCode.NOT_FOUND_MEMBER));

            seat.assignByMember(member);
        } catch (OptimisticLockException e) {
            log.error("optimistic lock exception", e);
        }
    }

    @Override
    @Transactional
    public void reservationTicket(TicketPaymentRequest ticketPaymentRequest) {
        Long seatId = ticketPaymentRequest.getSeatId();
        Seat seat =
                seatRepository
                        .findById(seatId)
                        .orElseThrow(() -> new TicketingException(ErrorCode.NOT_FOUND_SEAT));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = (String) authentication.getPrincipal();

        Member loginMember =
                memberRepository
                        .findByEmail(email)
                        .orElseThrow(() -> new TicketingException(ErrorCode.NOT_FOUND_MEMBER));

        if (!seat.getMember().getMemberId().equals(loginMember.getMemberId())) {
            throw new TicketingException(ErrorCode.NOT_SELECTABLE_SEAT);
        }
    }
}
