package com.weiyan.atp.data.request.web;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @author : 魏延thor
 * @since : 2020/6/16
 * 同意加入组织/声明组织属性
 */
@Data
public class ApproveOrgApplyRequest {
    @NotEmpty
    private String fileName;

    @NotEmpty
    private String orgName;

    private String attrName = "";
}
