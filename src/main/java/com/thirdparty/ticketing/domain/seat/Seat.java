package com.thirdparty.ticketing.domain.seat;

import jakarta.persistence.*;

import com.thirdparty.ticketing.domain.BaseEntity;
import com.thirdparty.ticketing.domain.zone.Zone;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "seat")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Seat extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seatId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "zone_id")
    private Zone zone;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_Grade")
    private SeatGrade seatGrade;

    @Column(length = 32)
    private String seatCode;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private SeatStatus seatStatus = SeatStatus.AVAILABLE;

    public Seat(String seatCode, SeatStatus seatStatus) {
        this.seatCode = seatCode;
        this.seatStatus = seatStatus;
    }
}
