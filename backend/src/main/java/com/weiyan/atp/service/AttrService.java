package com.weiyan.atp.service;

import com.weiyan.atp.constant.AttrApplyStatusEnum;
import com.weiyan.atp.data.bean.ChaincodeResponse;
import com.weiyan.atp.data.bean.DABEUser;
import com.weiyan.atp.data.bean.PlatAttr;
import com.weiyan.atp.data.request.web.ApplyUserAttrRequest;
import com.weiyan.atp.data.request.web.ApproveAttrApplyRequest;
import com.weiyan.atp.data.request.web.DeclareUserAttrRequest;
import com.weiyan.atp.data.request.web.RevokeUserAttrRequest;

/**
 * @author : 魏延thor
 * @since : 2020/6/11
 */
public interface AttrService {
    PlatAttr queryAttrByName(String attrName);

    ChaincodeResponse declareUserAttr(DeclareUserAttrRequest request);
    ChaincodeResponse declareUserAttr2(DeclareUserAttrRequest request);
    ChaincodeResponse batchDeclareUserAttr(DeclareUserAttrRequest request);

    ChaincodeResponse applyAttr(ApplyUserAttrRequest request);
    ChaincodeResponse applyAttr2(ApplyUserAttrRequest request);

    ChaincodeResponse revokeAttr(RevokeUserAttrRequest request);

    ChaincodeResponse batchApplyAttr(ApplyUserAttrRequest request);

    ChaincodeResponse queryAttrApply(String toUid, String toOrgId,
                                     String userName, AttrApplyStatusEnum status);
    ChaincodeResponse queryAttrHistory(String uid);

    ChaincodeResponse approveAttrApply(ApproveAttrApplyRequest request);
    ChaincodeResponse approveAttrApply2(ApproveAttrApplyRequest request);

    DABEUser syncSuccessAttrApply(String fileName);

    DABEUser syncSuccessAttrApply(String fileName, String toUid, String toOrgId);
    DABEUser syncSuccessAttrApply2(String fileName, String toUid, String toOrgId);
}
