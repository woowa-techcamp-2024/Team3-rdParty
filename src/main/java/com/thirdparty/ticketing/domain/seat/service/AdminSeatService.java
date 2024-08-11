package com.thirdparty.ticketing.domain.seat.service;

import com.thirdparty.ticketing.domain.seat.Seat;
import com.thirdparty.ticketing.domain.seat.dto.SeatCreationRequest;
import com.thirdparty.ticketing.domain.seat.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminSeatService {
    private final SeatRepository seatRepository;

    public void createSeats(Long zoneId, SeatCreationRequest seatCreationRequest) {
        //TODO: zone 구현 후 등록 필요
        List<Seat> seats = seatCreationRequest.getSeats().stream().map(seat ->
                    Seat.builder()
                            .zone(zoneId)
                            .seatCode(seat.getSeatCode())
                            .build()
        ).toList();

        seatRepository.saveAll(seats);
    }
}
