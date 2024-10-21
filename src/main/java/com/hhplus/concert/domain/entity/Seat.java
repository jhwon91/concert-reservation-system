package com.hhplus.concert.domain.entity;

import com.hhplus.concert.domain.enums.SeatStatus;
import jakarta.persistence.*;

@Entity
@Table(name = "seat")
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long concert_detail_id;
    private int seat_number;

    @Enumerated(EnumType.STRING)
    private SeatStatus status;
}
