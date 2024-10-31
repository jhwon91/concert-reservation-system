package com.hhplus.concert.domain.entity;

import com.hhplus.concert.domain.enums.SeatStatus;
import com.hhplus.concert.domain.support.error.CoreException;
import com.hhplus.concert.domain.support.error.ErrorType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "seat")
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "concert_detail_id")
    private Long concertDetailId;

    @Column(name = "seat_number")
    private int seatNumber;

    @Enumerated(EnumType.STRING)
    private SeatStatus status;

    @Version
    @Builder.Default
    private Long version = 0L;

    public Seat changeStatus(SeatStatus status){
        this.status = status;
        return this;
    }

    public void checkTemporaryStatus() {
        if(this.status != SeatStatus.TEMPORARY_ALLOCATED){
            throw new CoreException(ErrorType.SEAT_NOT_PAYABLE,this.status);
        }
    }

    public void checkAvailableStatus() {
        if(this.status != SeatStatus.AVAILABLE){
            throw new CoreException(ErrorType.SEAT_NOT_RESERVABLE,this.status);
        }
    }
}
