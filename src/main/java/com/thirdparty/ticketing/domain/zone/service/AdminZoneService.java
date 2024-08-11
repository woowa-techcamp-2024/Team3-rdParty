package com.thirdparty.ticketing.domain.zone.service;

import com.thirdparty.ticketing.domain.zone.Zone;
import com.thirdparty.ticketing.domain.zone.dto.ZoneCreationRequest;
import com.thirdparty.ticketing.domain.zone.repository.ZoneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminZoneService {
    private final ZoneRepository zoneRepository;

    @Transactional
    public void createZones(Long performanceId, ZoneCreationRequest zoneCreationRequest) {
        List<Zone> zones = convertDtoToEntity(performanceId, zoneCreationRequest);
        zoneRepository.saveAll(zones);
    }

    private List<Zone> convertDtoToEntity(Long performanceId, ZoneCreationRequest zoneCreationRequest) {
        //TODO: Performance 구현 후 필드 주입
        return zoneCreationRequest.getZones()
                .stream()
                .map(zoneElement ->
                        Zone.builder()
//                                .performance(performanceId)
                                .zoneName(zoneElement.getZoneName())
                                .build()
                ).toList();
    }
}
