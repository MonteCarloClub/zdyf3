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
public class ApplyUserAttrCCRequest extends BaseCCRequest {
    private String toUid;

    private String toOrgId;

    private Boolean isPublic;

    private String attrName;

    private String remark;

    @Override
    public String toString() {
        return "ApplyUserAttrCCRequest{" +
                "toUid='" + toUid + '\'' +
                ", toOrgId='" + toOrgId + '\'' +
                ", isPublic=" + isPublic +
                ", attrName='" + attrName + '\'' +
                ", remark='" + remark + '\'' +
                '}';
    }
}
