package com.weiyan.atp.data.bean;

import com.weiyan.atp.constant.ApplyTypeEnum;
import com.weiyan.atp.constant.AttrApplyStatusEnum;

import lombok.Data;

import java.util.Map;

/**
 * @author : 魏延thor
 * @since : 2020/6/8
 */
@Data
public class PlatUserAttrApply {
    private String fromUid;
    private String toUid;
    private String toOrgId;
    private Boolean isPublic;
    private String attrName;
    private String remark;
    private int n;
    private int t;
    /**
     * 0 toUser
     * 1 toOrg
     */
    private ApplyTypeEnum applyType;
    /**
     * 0 pending
     * 1 success
     * 2 fail
     * 3 all
     */
    private AttrApplyStatusEnum status;

    public void setStatus(int status) {
        this.status = AttrApplyStatusEnum.getByInt(status);
    }

    public void setApplyType(int applyType) {
        this.applyType = ApplyTypeEnum.getByInt(applyType);
    }

    private Map<String, ApplyApproval> approvalMap;

    @Data
    public static class ApplyApproval {
        private Boolean agree;
        private String secret;
        private String approveRemark;
    }
}
