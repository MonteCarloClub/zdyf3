package com.weiyan.atp.data.request.web;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @author : 魏延thor
 * @since : 2020/6/8
 */
@Data
public class SyncAttrRequest {
    @NotEmpty
    private String fileName;
    private String toId;
    private Integer type;
}
