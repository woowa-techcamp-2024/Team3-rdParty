package com.thirdparty.ticketing.domain.seat.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thirdparty.ticketing.domain.ItemResult;
import com.thirdparty.ticketing.domain.common.TicketingException;
import com.thirdparty.ticketing.domain.seat.dto.SeatElement;
import com.thirdparty.ticketing.domain.seat.repository.SeatRepository;
import com.thirdparty.ticketing.domain.zone.Zone;
import com.thirdparty.ticketing.domain.zone.repository.ZoneRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SeatService {
    private final ZoneRepository zoneRepository;
    private final SeatRepository seatRepository;

    @Transactional(readOnly = true)
    public ItemResult<SeatElement> getSeats(Long zoneId) {
        Zone zone = zoneRepository.findById(zoneId).orElseThrow(() -> new TicketingException(""));
        List<SeatElement> seats =
                seatRepository.findByZone(zone).stream().map(SeatElement::of).toList();

        return ItemResult.of(seats);
    }
}
