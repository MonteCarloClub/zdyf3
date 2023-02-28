package com.weiyan.atp.data.bean;

import com.weiyan.atp.constant.OrgApplyStatusEnum;
import com.weiyan.atp.constant.OrgApplyTypeEnum;

import lombok.Data;

import java.util.Map;

/**
 * @author : 魏延thor
 * @since : 2020/6/16
 */
@Data
public class PlatOrgApply {
    private String orgId;
    private Map<String, Boolean> uidMap;
    private Map<String, Map<String, String>> shareMap;
    private Map<String, String> opkMap;
    private Integer t;
    private Integer n;
    private String fromUserName;
    private OrgApplyStatusEnum status;
    private String createTime;
    private String attrName;
    private OrgApplyTypeEnum type;

    public void setFromUid(String fromUid) {
        this.fromUserName = fromUid;
    }

    public void setType(int type) {
        this.type = OrgApplyTypeEnum.valueOf(type);
    }
}
