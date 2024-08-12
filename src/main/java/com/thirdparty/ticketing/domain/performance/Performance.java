package com.thirdparty.ticketing.domain.performance;

import java.time.ZonedDateTime;

import jakarta.persistence.*;

import com.thirdparty.ticketing.domain.BaseEntity;

import lombok.*;

@Entity
@Table(name = "performance")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Performance extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long performanceId;

    private String performanceName;

    private String performancePlace;

    private ZonedDateTime performanceShowtime;
}
