package com.hhplus.concert.concert.domain.vo;

import com.hhplus.concert.common.exception.CoreException;
import com.hhplus.concert.common.exception.ErrorType;

import java.util.Objects;

public class SeatNumber {

    private static final int MIN = 1;
    private static final int MAX = 50;

    private final int value;

    private SeatNumber(int value) {
        if (value < MIN || value > MAX) {
            throw new CoreException(ErrorType.INVALID_SEAT_NUMBER, value);
        }
        this.value = value;
    }

    public static SeatNumber of(int value) {
        return new SeatNumber(value);
    }

    public int getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SeatNumber)) return false;
        return value == ((SeatNumber) o).value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
