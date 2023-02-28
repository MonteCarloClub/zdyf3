package com.weiyan.atp.data.response.intergration;

import lombok.Builder;
import lombok.Data;

/**
 * @author Eric
 * @date 2021/11/9 9:41
 */
@Data
@Builder
public class ApplyAttrResponse {
    private String remark;
    private String fromUserName;
    private String fromOrgName;
    private String toUserName;
    private String attrName;
    private String timeStamp;
    private String result;
}
