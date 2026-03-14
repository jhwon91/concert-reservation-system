package com.hhplus.concert.queue.domain.vo;

import java.util.Objects;
import java.util.UUID;

public class TokenValue {

    private final String value;

    private TokenValue(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("토큰 값은 비어있을 수 없습니다.");
        }
        this.value = value;
    }

    public static TokenValue generate() {
        return new TokenValue(UUID.randomUUID().toString());
    }

    public static TokenValue of(String value) {
        return new TokenValue(value);
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TokenValue)) return false;
        return Objects.equals(value, ((TokenValue) o).value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
