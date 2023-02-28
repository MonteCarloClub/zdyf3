package com.weiyan.atp.data.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.function.Function;

/**
 * @author : 魏延thor
 * @since : 2020/5/31
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChaincodeResponse {
    private Status status;
    private String message;
    private String txId;

    public enum Status {
        SUCCESS,
        FAIL
    }

    @JsonIgnore
    public Result<Object> getResult(Function<String, Object> converter) {
        if (status == Status.SUCCESS) {
            return Result.okWithData(converter.apply(message));
        } else {
            return Result.internalError(message);
        }
    }

    public boolean isFailed() {
        return this.status == Status.FAIL;
    }
}
