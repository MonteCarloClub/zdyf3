package com.weiyan.atp.service;

import com.weiyan.atp.data.bean.ChaincodeResponse;
import com.weiyan.atp.data.bean.PlatUser;
import com.weiyan.atp.data.request.web.CreateUserRequest;
import com.weiyan.atp.data.request.web.QueryUserRequest;

public interface UserRepositoryService {
    ChaincodeResponse createUser(CreateUserRequest request);

    ChaincodeResponse createUserInOne(String userName,String userType,String channel);

    PlatUser queryUser(QueryUserRequest request);
}