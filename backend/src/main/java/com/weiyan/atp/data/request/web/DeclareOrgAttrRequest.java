package com.weiyan.atp.data.request.web;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @author : 魏延thor
 * @since : 2020/7/2
 */
@Data
public class DeclareOrgAttrRequest {
    @NotEmpty
    private String fileName;

    @NotEmpty
    private String orgName;

    @NotEmpty
    private String attrName;
}
