package com.hhplus.concert.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommonResponse<T> {

    private final T data;
    private final String message;

    private CommonResponse(T data, String message) {
        this.data = data;
        this.message = message;
    }

    public static <T> CommonResponse<T> success(T data) {
        return new CommonResponse<>(data, null);
    }

    public static <T> CommonResponse<T> success() {
        return new CommonResponse<>(null, null);
    }

    public static <T> CommonResponse<T> error(T data, String message) {
        return new CommonResponse<>(data, message);
    }

    public static <T> CommonResponse<T> error(String message) {
        return new CommonResponse<>(null, message);
    }
}
