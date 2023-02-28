package com.weiyan.atp.data.bean;

import com.weiyan.atp.constant.ApplyTypeEnum;
import com.weiyan.atp.constant.AttrApplyStatusEnum;
import lombok.Data;

import java.util.Map;

@Data
public class PlatUserAttrHistory {
    private String uid;
    private String fromUid;
    private String attrName;
    private String operation;
    private String timeStamp;
}
