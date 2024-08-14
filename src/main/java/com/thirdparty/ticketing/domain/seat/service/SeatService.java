package com.thirdparty.ticketing.domain.seat.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thirdparty.ticketing.domain.ItemResult;
import com.thirdparty.ticketing.domain.common.ErrorCode;
import com.thirdparty.ticketing.domain.common.TicketingException;
import com.thirdparty.ticketing.domain.performance.Performance;
import com.thirdparty.ticketing.domain.performance.repository.PerformanceRepository;
import com.thirdparty.ticketing.domain.seat.dto.response.SeatElement;
import com.thirdparty.ticketing.domain.seat.dto.response.SeatGradeElement;
import com.thirdparty.ticketing.domain.seat.repository.SeatGradeRepository;
import com.thirdparty.ticketing.domain.seat.repository.SeatRepository;
import com.thirdparty.ticketing.domain.zone.Zone;
import com.thirdparty.ticketing.domain.zone.repository.ZoneRepository;

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
}
