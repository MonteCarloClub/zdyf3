package com.weiyan.atp.data.request.chaincode.plat;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.weiyan.atp.constant.OrgApplyTypeEnum;

import lombok.Builder;
import lombok.Data;

/**
 * @author : 魏延thor
 * @since : 2020/6/1
 */
@Data
@Builder
@JsonPropertyOrder(alphabetic = true)
public class QueryOrgApplyCCRequest {
    private String orgId;
    private String attrName;
    private Integer status;
    private OrgApplyTypeEnum type;

    public Integer getType() {
        return type.getSceneType();
    }
}
