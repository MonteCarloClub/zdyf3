package com.weiyan.atp.data.request.web;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @author : 魏延thor
 * @since : 2020/6/1
 */
@Data
public class ApplyUserAttrRequest {
    @NotEmpty
    private String fileName;
    private String toUserName;
    private String toOrgName;

    private Boolean isPublic = true;

    @NotEmpty
    private String attrName;

    private String remark;
}
