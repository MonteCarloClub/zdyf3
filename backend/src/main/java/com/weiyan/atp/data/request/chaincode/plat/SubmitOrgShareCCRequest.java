package com.weiyan.atp.data.request.chaincode.plat;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.weiyan.atp.constant.OrgApplyTypeEnum;

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
public class SubmitOrgShareCCRequest extends BaseCCRequest {
    private OrgApplyTypeEnum type;

    private String orgId;

    private String attrName;

    private String toUid;

    private String share;

    public int getType() {
        return type.getSceneType();
    }
}
