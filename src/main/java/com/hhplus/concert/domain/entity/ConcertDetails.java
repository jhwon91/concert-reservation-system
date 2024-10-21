package com.hhplus.concert.domain.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "concert_detail")
public class ConcertDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long concert_id;
    private Long max_seat;
    private Long price;
    private LocalDate concert_date;
}
