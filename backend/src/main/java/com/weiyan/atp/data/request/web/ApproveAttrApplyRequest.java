package com.weiyan.atp.data.request.web;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author : 魏延thor
 * @since : 2020/6/8
 * 同意其他用户的属性申请
 */
@Data
public class ApproveAttrApplyRequest {
    @NotEmpty
    private String fileName;
    @NotEmpty
    private String toUserName;
    @NotEmpty
    private String attrName;

    private String remark;
    @NotNull
    private Boolean agree;
}
