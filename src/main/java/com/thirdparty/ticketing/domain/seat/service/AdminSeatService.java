package com.thirdparty.ticketing.domain.seat.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thirdparty.ticketing.domain.seat.Seat;
import com.thirdparty.ticketing.domain.seat.SeatGrade;
import com.thirdparty.ticketing.domain.seat.repository.SeatGradeRepository;
import com.thirdparty.ticketing.domain.seat.dto.SeatCreationRequest;
import com.thirdparty.ticketing.domain.seat.dto.SeatGradeCreationRequest;
import com.thirdparty.ticketing.domain.seat.repository.SeatRepository;
import com.thirdparty.ticketing.domain.zone.Zone;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminSeatService {
    private final SeatRepository seatRepository;
    private final SeatGradeRepository seatGradeRepository;

    @Transactional
    public void createSeats(Long zoneId, SeatCreationRequest seatCreationRequest) {
        List<Seat> seats = convertDtoToEntity(zoneId, seatCreationRequest);
        seatRepository.saveAll(seats);
    }

    private List<Seat> convertDtoToEntity(Long zoneId, SeatCreationRequest seatCreationRequest) {
        return seatCreationRequest.getSeats().stream()
                .map(
                        seat ->
                                Seat.builder()
                                        .zone(Zone.builder().zoneId(zoneId).build())
                                        .seatCode(seat.getSeatCode())
                                        .build())
                .toList();
    }

    @Transactional
    public void createSeatGrades(long performanceId, SeatGradeCreationRequest seatGradeCreationRequest) {
        List<SeatGrade> seatGrades = seatGradeCreationRequest.getSeatGrades()
                .stream()
                .map(seatGradeElement -> seatGradeElement.toEntity(performanceId))
                .toList();

        seatGradeRepository.saveAll(seatGrades);
    }
}
