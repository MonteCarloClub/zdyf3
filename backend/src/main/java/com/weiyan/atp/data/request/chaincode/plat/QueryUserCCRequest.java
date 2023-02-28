package com.weiyan.atp.data.request.chaincode.plat;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Builder;
import lombok.Data;

/**
 * @author : 魏延thor
 * @since : 2020/6/1
 */
@Data
@Builder
@JsonPropertyOrder(alphabetic = true)
public class QueryUserCCRequest {
    private String publicKey;
    private String uid;
}
