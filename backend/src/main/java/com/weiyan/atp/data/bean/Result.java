package com.weiyan.atp.data.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author : 魏延thor
 * @since : 2020/6/1
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {
    private int code;
    private String message;
    private T data;

    public static <T> Result<T> okWithData(T data) {
        return Result.<T>builder()
            .code(200)
            .data(data)
            .build();
    }

    public static <T> Result<T> success() {
        return Result.<T>builder()
            .code(200)
            .build();
    }

    public static <T> Result<T> failWithMessage(int code, String message) {
        return Result.<T>builder()
            .code(code)
            .message(message)
            .build();
    }

    public static <T> Result<T> internalError(String message) {
        return failWithMessage(500, message);
    }
}
