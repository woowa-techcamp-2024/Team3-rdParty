package com.thirdparty.ticketing.domain.seat;

import jakarta.persistence.*;

import com.thirdparty.ticketing.domain.BaseEntity;
import com.thirdparty.ticketing.domain.performance.Performance;

import lombok.*;

@Entity
@Table(name = "seat_grade")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SeatGrade extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seatGradeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performance_id")
    private Performance performance;

    @Column(nullable = false)
    private Long price;

    @Column(nullable = false, length = 32)
    private String gradeName;
}
