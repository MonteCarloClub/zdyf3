package com.weiyan.atp.data.response.intergration;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author Eric
 * @date 2021/11/8 19:23
 */
@Data
@Builder
public class DecryptionResponse {
    private String contentHash;
    private String uid;
    private List<String> tags;   //tags[0]:城市  tags[1]:系统   tags[2]:业务   tags[3]:文件名
    private String timeStamp;
    private String plainText;
}
