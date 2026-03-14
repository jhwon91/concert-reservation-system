package com.hhplus.concert.concert.domain;

import com.hhplus.concert.common.entity.BaseEntity;
import com.hhplus.concert.common.exception.CoreException;
import com.hhplus.concert.common.exception.ErrorType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "concert_dates")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ConcertDate extends BaseEntity {

    @Id
    @Column(name = "concert_date_id")
    private UUID concertDateId;

    @Column(nullable = false)
    private UUID concertId;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private int totalSeats;

    @Column(nullable = false)
    private int availableSeats;

    public ConcertDate(UUID concertId, LocalDate date, int totalSeats) {
        this.concertDateId = UUID.randomUUID();
        this.concertId = concertId;
        this.date = date;
        this.totalSeats = totalSeats;
        this.availableSeats = totalSeats;
    }

    public void decreaseAvailableSeats() {
        if (this.availableSeats <= 0) {
            throw new CoreException(ErrorType.SEAT_NOT_AVAILABLE, this.availableSeats);
        }
        this.availableSeats--;
    }

    public void increaseAvailableSeats() {
        if (this.availableSeats >= this.totalSeats) {
            throw new CoreException(ErrorType.SEAT_NOT_AVAILABLE, this.availableSeats);
        }
        this.availableSeats++;
    }
}
