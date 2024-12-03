package com.thirdparty.ticketing.domain.ticket.service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import com.thirdparty.ticketing.domain.common.ErrorCode;
import com.thirdparty.ticketing.domain.common.EventPublisher;
import com.thirdparty.ticketing.domain.common.TicketingException;
import com.thirdparty.ticketing.domain.member.Member;
import com.thirdparty.ticketing.domain.member.repository.MemberRepository;
import com.thirdparty.ticketing.domain.seat.Seat;
import com.thirdparty.ticketing.domain.seat.SeatStatus;
import com.thirdparty.ticketing.domain.seat.repository.SeatRepository;
import com.thirdparty.ticketing.domain.ticket.dto.event.SeatEvent;
import com.thirdparty.ticketing.domain.ticket.dto.request.SeatSelectionRequest;
import com.thirdparty.ticketing.domain.ticket.dto.request.TicketPaymentRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class ReservationNonTransactionService implements ReservationService {

    private final MemberRepository memberRepository;
    private final SeatRepository seatRepository;
    private final EventPublisher eventPublisher;

    private final ReservationManager reservationManager;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(5);

    @Value("${ticketing.reservation.release-delay-seconds}")
    private int reservationReleaseDelay;

    @Override
    public void selectSeat(String memberEmail, SeatSelectionRequest seatSelectionRequest) {
        Long seatId = seatSelectionRequest.getSeatId();

        Seat seat =
                seatRepository.findById(seatId)
                        .orElseThrow(() -> new TicketingException(ErrorCode.NOT_FOUND_SEAT));

        Member member =
                memberRepository
                        .findByEmail(memberEmail)
                        .orElseThrow(() -> new TicketingException(ErrorCode.NOT_FOUND_MEMBER));

        int ret = seatRepository.updateOptimisticSeat(seat, member, SeatStatus.SELECTED, SeatStatus.SELECTABLE);

        if (ret == 0) {
            throw new TicketingException(ErrorCode.NOT_SELECTABLE_SEAT);
        }

        // 좌석 선택 이벤트 발행
        // eventPublisher.publish(new SeatEvent(memberEmail, seatId, SeatEvent.EventType.SELECT));

        // scheduler.schedule(
        //        () -> reservationManager.releaseSeat(member, seatId),
        //        reservationReleaseDelay,
        //        TimeUnit.SECONDS);
    }

    @Override
    public void reservationTicket(String memberEmail, TicketPaymentRequest ticketPaymentRequest) {
        // 구현 안함
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
}
