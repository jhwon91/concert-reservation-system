package com.hhplus.concert.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDate;

@Entity
@Getter
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
