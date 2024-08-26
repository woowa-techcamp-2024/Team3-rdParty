package com.thirdparty.ticketing.seat.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thirdparty.common.ItemResult;
import com.thirdparty.ticketing.exception.ErrorCode;
import com.thirdparty.ticketing.exception.TicketingException;
import com.thirdparty.ticketing.jpa.performance.Performance;
import com.thirdparty.ticketing.jpa.performance.PerformanceRepository;
import com.thirdparty.ticketing.jpa.seat.repository.SeatGradeRepository;
import com.thirdparty.ticketing.jpa.seat.repository.SeatRepository;
import com.thirdparty.ticketing.jpa.zone.Zone;
import com.thirdparty.ticketing.jpa.zone.repository.ZoneRepository;
import com.thirdparty.ticketing.seat.dto.response.SeatElement;
import com.thirdparty.ticketing.seat.dto.response.SeatGradeElement;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SeatService {
    private final PerformanceRepository performanceRepository;
    private final ZoneRepository zoneRepository;
    private final SeatGradeRepository seatGradeRepository;
    private final SeatRepository seatRepository;

    @Transactional(readOnly = true)
    public ItemResult<SeatElement> getSeats(Long zoneId) {
        Zone zone =
                zoneRepository
                        .findById(zoneId)
                        .orElseThrow(() -> new TicketingException(ErrorCode.NOT_FOUND_ZONE));
        List<SeatElement> seats =
                seatRepository.findByZone(zone).stream().map(SeatElement::of).toList();

        return ItemResult.of(seats);
    }

    @Transactional(readOnly = true)
    public ItemResult<SeatGradeElement> getSeatGrades(Long performanceId) {
        Performance performance =
                performanceRepository
                        .findById(performanceId)
                        .orElseThrow(() -> new TicketingException(ErrorCode.NOT_FOUND_ZONE));
        List<SeatGradeElement> seatGrades =
                seatGradeRepository.findAllByPerformance(performance).stream()
                        .map(SeatGradeElement::of)
                        .toList();

        return ItemResult.of(seatGrades);
    }

    public ItemResult<SeatElement> getAllSeats(long performanceId) {
        List<SeatElement> seats =
                seatRepository.findByPerformanceId(performanceId).stream()
                        .map(SeatElement::of)
                        .toList();
        return ItemResult.of(seats);
    }
}
