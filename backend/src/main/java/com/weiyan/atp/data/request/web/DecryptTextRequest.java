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
    private String cipherText;    // 要解密的内容【这个在dabe.decrypt()里好像没用到啊】

//    @NotEmpty
//    private String policy;      // 密文的策略表达式

    @NotEmpty
    private String userID;  // 发起解密请求的用户id

    @NotEmpty
    private String caseID;  // 解密的病例ID
    @NotEmpty
    private String fieldName;   // 解密的caseID下的字段名
    // 区块链上dabe.decrypt()从encrypt/{SharedUser}/{FileName}文件读取密文
    // 我们用caseID作为SharedUser，fieldName作为FileName
}
