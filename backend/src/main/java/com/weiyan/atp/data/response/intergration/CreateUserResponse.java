package com.weiyan.atp.data.response.intergration;

import lombok.Builder;
import lombok.Data;

/**
 * @author Eric
 * @date 2021/11/9 10:49
 */
@Data
@Builder
public class CreateUserResponse {
    private String channel;
    private String cert;
    private String uid;
    private String timeStamp;
    private String result;
}
