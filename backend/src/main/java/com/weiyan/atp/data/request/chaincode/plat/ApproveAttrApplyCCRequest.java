package com.weiyan.atp.data.request.chaincode.plat;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

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
public class ApproveAttrApplyCCRequest extends BaseCCRequest {
    private String fromUid;

    private String toOrgId;

    private Boolean agree;

    private String attrName;

    private String remark;

    private String secret;
}
