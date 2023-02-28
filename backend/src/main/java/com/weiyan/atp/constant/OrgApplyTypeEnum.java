package com.weiyan.atp.constant;

import lombok.Getter;

import java.util.Arrays;

/**
 * @author : 魏延thor
 * @since : 2020/6/16
 */
@Getter
public enum OrgApplyTypeEnum {
    CREATION("/org/approveOrgApply", "/org/queryOrgApply", 0),
    ATTRIBUTE("/org/approveOrgApply", "/org/queryOrgApply", 1),
    ;

    private String approveFunctionName;
    private String queryFunctionName;

    private int sceneType;

    OrgApplyTypeEnum(String approveFunctionName, String queryFunctionName, int sceneType) {
        this.approveFunctionName = approveFunctionName;
        this.queryFunctionName = queryFunctionName;
        this.sceneType = sceneType;
    }

    public static OrgApplyTypeEnum valueOf(int sceneType) {
        return Arrays.stream(OrgApplyTypeEnum.values())
            .filter(type -> type.sceneType == sceneType)
            .findAny()
            .orElseThrow(() -> new BaseException("no match OrgApplyTypeEnum"));
    }
}
