package com.thirdparty.ticketing.domain.seat;

import com.thirdparty.ticketing.domain.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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

    // TODO: ManyToOne Mapping
    private Object zone;

    private String seatCode;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private SeatStatus seatStatus = SeatStatus.AVAILABLE;

    public Seat(String seatCode, SeatStatus seatStatus) {
        this.seatCode = seatCode;
        this.seatStatus = seatStatus;
    }
}
