package com.weiyan.atp.data.request.web;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class RevokeUserAttrRequest {
    @NotEmpty
    private String userName;
    @NotEmpty
    private String toUserName;

    @NotEmpty
    private String attrName;

    private String remark;
}
