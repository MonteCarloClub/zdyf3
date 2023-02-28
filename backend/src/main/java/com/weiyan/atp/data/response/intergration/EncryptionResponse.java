package com.weiyan.atp.data.response.intergration;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author Eric
 * @date 2021/11/8 16:02
 */
@Data
@Builder
public class EncryptionResponse {
    private String cipherHash;
    private String cipher;
    private String policy;
    private String uid;
    private List<String> tags;   //tags[0]:城市  tags[1]:系统   tags[2]:业务   tags[3]:文件名
    private String timeStamp;
}

