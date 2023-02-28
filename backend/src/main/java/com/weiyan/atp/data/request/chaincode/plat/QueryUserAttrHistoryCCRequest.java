package com.weiyan.atp.data.request.chaincode.plat;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonPropertyOrder(alphabetic = true)
public class QueryUserAttrHistoryCCRequest {
    private String uid;
}
