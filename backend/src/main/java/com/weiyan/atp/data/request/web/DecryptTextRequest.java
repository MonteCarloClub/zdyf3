package com.weiyan.atp.data.request.web;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @author BerryChen0w0
 * @date  2024-06-21
 */
@Data
@AllArgsConstructor
public class DecryptTextRequest {
    @NotEmpty
    private String cipherText;    // 要解密的内容

//    @NotEmpty
//    private String policy;      // 密文的策略表达式

    @NotEmpty
    private String userID;  // 发起解密请求的用户id
}
