package com.weiyan.atp.data.request.chaincode.plat;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Builder.Default;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

/**
 * @author : 魏延thor
 * @since : 2020/6/1
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@JsonPropertyOrder(alphabetic = true)
public class QueryContentsCCRequest extends BaseCCRequest {
    private String fromUid;
    private String tag;
    @Default
    private Integer pageSize = 10;
    @Default
    private String bookmark = "";
}
