package com.weiyan.atp.data.request.chaincode.plat;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * @author : 魏延thor
 * @since : 2020/6/1
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@JsonPropertyOrder(alphabetic = true)
public class CreateOrgCCRequest extends BaseCCRequest {
    private Integer t;

    private Integer n;

    private List<String> uidList;

    private String orgId;

    private String userStr;
}
