package com.weiyan.atp.constant;

import lombok.Data;

/**
 * @author : 魏延thor
 * @since : 2020/5/31
 */
@Data
public class BaseException extends RuntimeException {
    private final int errorCode;

    public BaseException(int errorCode, String message) {
        this(errorCode, message, null);
    }

    public BaseException(String message) {
        this(-1, message, null);
    }

    public BaseException(String message, Throwable cause) {
        this(-1, message, cause);
    }

    private BaseException(int errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
}
