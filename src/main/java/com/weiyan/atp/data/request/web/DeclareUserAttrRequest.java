package com.weiyan.atp.data.request.web;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @author : 魏延thor
 * @since : 2020/6/1
 */
@Data
public class DeclareUserAttrRequest {
    @NotEmpty
    private String attrName;
    @NotEmpty
    private String fileName;
}
