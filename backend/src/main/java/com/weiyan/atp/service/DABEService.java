package com.weiyan.atp.service;

import com.weiyan.atp.data.bean.ChaincodeResponse;
import com.weiyan.atp.data.bean.DABEUser;

import javax.validation.constraints.NotEmpty;

/**
 * @author : 魏延thor
 * @since : 2020/6/2
 *
 * 默认是给本地用户自己使用的，所以根据文件名获取user
 */
public interface DABEService {
    DABEUser getUser(@NotEmpty String fileName);

    DABEUser getUser2(@NotEmpty String fileName, @NotEmpty String password);

    DABEUser getUser3(@NotEmpty String fileName, @NotEmpty String cert);

    DABEUser createUser(@NotEmpty String fileName, @NotEmpty String userName);

    DABEUser createUser(@NotEmpty String fileName, @NotEmpty String userName, @NotEmpty String userType, @NotEmpty String channel, @NotEmpty String password);

    DABEUser declareAttr(@NotEmpty String fileName, @NotEmpty String attrName);

    ChaincodeResponse approveAttrApply(@NotEmpty String fileName, @NotEmpty String attrName,
                                       @NotEmpty String toUserName);
}
