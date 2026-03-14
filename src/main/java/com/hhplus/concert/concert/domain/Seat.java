package com.hhplus.concert.concert.domain;

import com.hhplus.concert.common.entity.BaseEntity;
import com.hhplus.concert.common.exception.CoreException;
import com.hhplus.concert.common.exception.ErrorType;
import com.hhplus.concert.concert.domain.vo.SeatNumber;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "seats")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Seat extends BaseEntity {

    @Id
    @Column(name = "seat_id")
    private UUID seatId;

    @Column(nullable = false)
    private UUID concertDateId;

    @Column(nullable = false)
    private int seatNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SeatStatus status;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal price;

    @Version
    private Long version;

    public Seat(UUID concertDateId, SeatNumber seatNumber, BigDecimal price) {
        this.seatId = UUID.randomUUID();
        this.concertDateId = concertDateId;
        this.seatNumber = seatNumber.getValue();
        this.status = SeatStatus.AVAILABLE;
        this.price = price;
    }

    public void reserve() {
        if (this.status != SeatStatus.AVAILABLE) {
            throw new CoreException(ErrorType.SEAT_NOT_AVAILABLE, this.status);
        }
        this.status = SeatStatus.RESERVED;
    }

    public void occupy() {
        if (this.status != SeatStatus.RESERVED) {
            throw new CoreException(ErrorType.SEAT_NOT_RESERVED, this.status);
        }
        this.status = SeatStatus.OCCUPIED;
    }

    public void release() {
        if (this.status == SeatStatus.OCCUPIED) {
            throw new CoreException(ErrorType.SEAT_NOT_RESERVED, this.status);
        }
        this.status = SeatStatus.AVAILABLE;
    }

    public SeatNumber getSeatNumber() {
        return SeatNumber.of(this.seatNumber);
    }
}
