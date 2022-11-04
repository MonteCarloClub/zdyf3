package com.weiyan.atp.data.request.chaincode.plat;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Builder.Default;
import lombok.Data;
import lombok.experimental.SuperBuilder;

/**
 * @author : 魏延thor
 * @since : 2020/6/1
 */
@Data
@SuperBuilder
public class BaseCCRequest {
    protected String uid;
    @Default
    protected String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
    @JsonInclude(value = Include.NON_EMPTY)
    protected String sign;
}
