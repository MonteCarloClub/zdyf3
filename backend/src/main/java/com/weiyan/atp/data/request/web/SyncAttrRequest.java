package com.weiyan.atp.data.request.web;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @author : 魏延thor
 * @since : 2020/6/8
 */
@Builder
@Data
public class SyncAttrRequest {
    @NotEmpty
    private String fileName;
    private String toId;
    private Integer type;
}
