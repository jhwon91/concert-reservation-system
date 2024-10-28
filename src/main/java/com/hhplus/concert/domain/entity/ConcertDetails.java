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

    @Column(name = "concert_id")
    private Long concertId;

    @Column(name = "max_seat")
    private Long maxSeat;

    private Long price;

    @Column(name = "concert_date")
    private LocalDate concertDate;
}
