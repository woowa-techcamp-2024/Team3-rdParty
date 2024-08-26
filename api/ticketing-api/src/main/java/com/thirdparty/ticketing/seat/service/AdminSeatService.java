package com.thirdparty.ticketing.seat.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thirdparty.ticketing.exception.ErrorCode;
import com.thirdparty.ticketing.exception.TicketingException;
import com.thirdparty.ticketing.jpa.performance.Performance;
import com.thirdparty.ticketing.jpa.performance.PerformanceRepository;
import com.thirdparty.ticketing.jpa.seat.Seat;
import com.thirdparty.ticketing.jpa.seat.SeatGrade;
import com.thirdparty.ticketing.jpa.seat.repository.SeatGradeRepository;
import com.thirdparty.ticketing.jpa.seat.repository.SeatRepository;
import com.thirdparty.ticketing.jpa.zone.Zone;
import com.thirdparty.ticketing.jpa.zone.repository.ZoneRepository;
import com.thirdparty.ticketing.seat.dto.request.SeatCreationElement;
import com.thirdparty.ticketing.seat.dto.request.SeatCreationRequest;
import com.thirdparty.ticketing.seat.dto.request.SeatGradeCreationRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminSeatService {
    private final SeatRepository seatRepository;
    private final SeatGradeRepository seatGradeRepository;
    private final PerformanceRepository performanceRepository;
    private final ZoneRepository zoneRepository;

    @Transactional
    public void createSeatGrades(
            long performanceId, SeatGradeCreationRequest seatGradeCreationRequest) {
        List<SeatGrade> seatGrades = convertDtoToEntity(performanceId, seatGradeCreationRequest);

        seatGradeRepository.saveAll(seatGrades);
    }

    private List<SeatGrade> convertDtoToEntity(
            long performanceId, SeatGradeCreationRequest seatGradeCreationRequest) {
        Performance performance =
                performanceRepository
                        .findById(performanceId)
                        .orElseThrow(() -> new TicketingException(ErrorCode.NOT_FOUND_PERFORMANCE));

        return seatGradeCreationRequest.getSeatGrades().stream()
                .map(
                        seatGrade ->
                                SeatGrade.builder()
                                        .performance(performance)
                                        .price(seatGrade.getPrice())
                                        .gradeName(seatGrade.getGradeName())
                                        .build())
                .toList();
    }

    @Transactional
    public void createSeats(
            long performanceId, long zoneId, SeatCreationRequest seatCreationRequest) {
        List<Seat> seats = convertDtoToEntity(performanceId, zoneId, seatCreationRequest);
        seatRepository.saveAll(seats);
    }

    private List<Seat> convertDtoToEntity(
            long performanceId, long zoneId, SeatCreationRequest seatCreationRequest) {
        Map<Long, SeatGrade> seatGradeMap = findSeatGrades(performanceId, seatCreationRequest);

        Zone zone =
                zoneRepository
                        .findById(zoneId)
                        .orElseThrow(() -> new TicketingException(ErrorCode.NOT_FOUND_ZONE));

        return seatCreationRequest.getSeats().stream()
                .map(
                        seat ->
                                Seat.builder()
                                        .zone(zone)
                                        .seatGrade(seatGradeMap.get(seat.getGradeId()))
                                        .seatCode(seat.getSeatCode())
                                        .build())
                .toList();
    }

    /** N+1 문제를 방지하기 위해 요청된 gradeName 목록을 미리 조회합니다. Map<gradeName, SeatGrade> 형태로 구조화해서 반환합니다. */
    private Map<Long, SeatGrade> findSeatGrades(
            long performanceId, SeatCreationRequest seatCreationRequest) {
        List<Long> gradeIds =
                seatCreationRequest.getSeats().stream()
                        .map(SeatCreationElement::getGradeId)
                        .distinct()
                        .toList();

        List<SeatGrade> seatGrades =
                seatGradeRepository.findByPerformanceIdAndGradeNames(performanceId, gradeIds);
        Map<Long, SeatGrade> seatGradeMap =
                seatGrades.stream()
                        .collect(
                                Collectors.toMap(
                                        SeatGrade::getSeatGradeId, seatGrade -> seatGrade));

        if (seatGradeMap.size() != gradeIds.size()) {
            throw new TicketingException(ErrorCode.NOT_FOUND_SEAT_GRADE);
        }

        return seatGradeMap;
    }
}
