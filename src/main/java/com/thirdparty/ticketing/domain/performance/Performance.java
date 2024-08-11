package com.thirdparty.ticketing.domain.performance;

import com.thirdparty.ticketing.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.time.ZonedDateTime;

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
