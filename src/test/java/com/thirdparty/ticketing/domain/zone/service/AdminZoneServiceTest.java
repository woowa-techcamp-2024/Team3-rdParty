package com.thirdparty.ticketing.domain.zone.service;

import com.thirdparty.ticketing.domain.zone.Zone;
import com.thirdparty.ticketing.domain.zone.dto.ZoneCreationElement;
import com.thirdparty.ticketing.domain.zone.dto.ZoneCreationRequest;
import com.thirdparty.ticketing.domain.zone.repository.ZoneRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@DataJpaTest
public class AdminZoneServiceTest {
    private AdminZoneService adminZoneService;
    @Autowired
    private ZoneRepository zoneRepository;

    @BeforeEach
    void setUpBase() {
        adminZoneService = new AdminZoneService(zoneRepository);
    }

    @Nested
    @DisplayName("createZones 메서드를 호출할 때")
    class CreateZonesTest {

        private long performanceId;
        private ZoneCreationRequest zoneCreationRequest;

        @BeforeEach
        void setUp() {
            performanceId = 1L;
            zoneCreationRequest = new ZoneCreationRequest();
            ZoneCreationElement zone1 = new ZoneCreationElement();
            zone1.setZoneName("VIP");
            ZoneCreationElement zone2 = new ZoneCreationElement();
            zone2.setZoneName("General");

            zoneCreationRequest.setZones(List.of(
                    zone1, zone2
            ));
        }

        @Test
        @DisplayName("존이 성공적으로 생성된다.")
        void createZones_Success() {
            // When
            adminZoneService.createZones(performanceId, zoneCreationRequest);

            // Then
            List<Zone> zones = zoneRepository.findAll();
            assertThat(zones).hasSize(2);
            assertThat(zones.get(0).getZoneName()).isEqualTo("VIP");
            assertThat(zones.get(1).getZoneName()).isEqualTo("General");
        }
    }
}
