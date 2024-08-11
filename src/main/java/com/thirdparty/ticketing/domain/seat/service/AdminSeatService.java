package com.thirdparty.ticketing.domain.seat.service;

import com.thirdparty.ticketing.domain.seat.Seat;
import com.thirdparty.ticketing.domain.seat.dto.SeatCreationRequest;
import com.thirdparty.ticketing.domain.seat.repository.SeatRepository;
import com.thirdparty.ticketing.domain.zone.Zone;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminSeatService {
    private final SeatRepository seatRepository;

    public void createSeats(Long zoneId, SeatCreationRequest seatCreationRequest) {
        List<Seat> seats = convertDtoToEntity(zoneId, seatCreationRequest);
        seatRepository.saveAll(seats);
    }

    private List<Seat> convertDtoToEntity(Long zoneId, SeatCreationRequest seatCreationRequest) {
        return seatCreationRequest.getSeats().stream().map(seat ->
                Seat.builder()
                        .zone(Zone.builder().zoneId(zoneId).build())
                        .seatCode(seat.getSeatCode())
                        .build()
        ).toList();
    }
}
