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
    private String text;    // 被要加密的数据，实际上是fieldName

    @NotEmpty
    private String policy;  // 加密的属性策略

    @NotEmpty
    private String userID;  // 发起加密请求的用户id

    @NotEmpty
    private String caseID;  // 被加密的病例的id
    // 注：因为链上合约dabe.encrypt()要用到UserName和FileName，原本是把密文存在encrypt/UserName(文件的上传者)/FileName(上传的文件名)
    // 文件夹里。我们适配华山医院数据库使用场景，改为在encrypt/caseID/fieldID里存fieldID字段的密文，也就是说，把caseID设置为UserName，
    // fieldID设置为FileName
}
