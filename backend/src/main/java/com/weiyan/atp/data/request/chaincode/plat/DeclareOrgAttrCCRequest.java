package com.weiyan.atp.data.request.chaincode.plat;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

/**
 * @author : 魏延thor
 * @since : 2020/7/2
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@JsonPropertyOrder(alphabetic = true)
public class DeclareOrgAttrCCRequest extends BaseCCRequest {
    private String orgId;

    private String attrName;
}
