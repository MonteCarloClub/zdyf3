package com.weiyan.atp.data.request.web;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @author BerryChen0w0
 * @date 2024-06-20
 */
@Data
@AllArgsConstructor
public class EncryptTextRequest {
    @NotEmpty
    private String text;    // 被要加密的数据

    @NotEmpty
    private String policy;  // 加密的属性策略

    @NotEmpty
    private String userID;  // 发起加密请求的用户id
}
