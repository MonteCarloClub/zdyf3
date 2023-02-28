package com.weiyan.atp.service;

import com.weiyan.atp.constant.OrgApplyTypeEnum;
import com.weiyan.atp.data.bean.ChaincodeResponse;
import com.weiyan.atp.data.bean.PlatOrg;
import com.weiyan.atp.data.bean.PlatOrgApply;
import com.weiyan.atp.data.request.web.ApproveOrgApplyRequest;
import com.weiyan.atp.data.request.web.CreateOrgRequest;
import com.weiyan.atp.data.request.web.DeclareOrgAttrRequest;

import javax.validation.constraints.NotEmpty;

public interface OrgRepositoryService {
    /**
     * 申请创建组织
     */
    ChaincodeResponse applyCreateOrg(CreateOrgRequest request);

    ChaincodeResponse applyCreateOrg2(CreateOrgRequest request);

    /**
     * 申请声明属性
     */
    ChaincodeResponse applyDeclareOrgAttr(DeclareOrgAttrRequest request);
    ChaincodeResponse applyDeclareOrgAttr2(DeclareOrgAttrRequest request);

    /**
     * 同意加入组织/声明属性
     */
    void approveOrgApply(OrgApplyTypeEnum type, ApproveOrgApplyRequest request);
    void approveOrgApply2(OrgApplyTypeEnum type, ApproveOrgApplyRequest request);

    /**
     * 提交自己的part pk
     */
    void submitPartPk(OrgApplyTypeEnum type, String orgName, String fileName, String attrName);
    void submitPartPk2(OrgApplyTypeEnum type, String orgName, String fileName, String attrName);

    /**
     * 整合part pk
     */
    void mixPartPk(OrgApplyTypeEnum type, String orgName, String attrName, String fileName);
    void mixPartPk2(OrgApplyTypeEnum type, String orgName, String attrName, String fileName);

    PlatOrg queryOrg(@NotEmpty String orgName);

    PlatOrgApply queryOrgApply(@NotEmpty String orgName, OrgApplyTypeEnum type, String attrName);
}