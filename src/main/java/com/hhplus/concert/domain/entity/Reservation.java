package com.hhplus.concert.domain.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Builder
@Getter
@Table(name = "reservation")
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long concert_detail_id;
    private Long user_id;
    private Long seat_id;
    private String status;
    private LocalDateTime reservation_at;
}
