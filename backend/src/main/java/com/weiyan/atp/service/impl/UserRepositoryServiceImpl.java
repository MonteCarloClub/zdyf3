package com.weiyan.atp.service.impl;

import com.google.common.base.Preconditions;

import com.weiyan.atp.constant.BaseException;
import com.weiyan.atp.constant.ChaincodeTypeEnum;
import com.weiyan.atp.data.bean.ChaincodeResponse;
import com.weiyan.atp.data.bean.DABEUser;
import com.weiyan.atp.data.bean.PlatUser;
import com.weiyan.atp.data.request.chaincode.plat.CreateUserCCRequest;
import com.weiyan.atp.data.request.chaincode.plat.QueryUserCCRequest;
import com.weiyan.atp.data.request.web.CreateUserRequest;
import com.weiyan.atp.data.request.web.QueryUserRequest;
import com.weiyan.atp.service.ChaincodeService;
import com.weiyan.atp.service.DABEService;
import com.weiyan.atp.service.UserRepositoryService;
import com.weiyan.atp.utils.CCUtils;
import com.weiyan.atp.utils.JsonProviderHolder;
import com.weiyan.atp.app.controller.CommonController;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @author : 魏延thor
 * @since : 2020/5/30
 */
@Service
@Slf4j
public class UserRepositoryServiceImpl implements UserRepositoryService {
    private final ChaincodeService chaincodeService;
    private final DABEService dabeService;

    @Value("${atp.path.privateKey}")
    private String priKeyPath;//atp/priKey/
    @Value("${atp.path.publicKey}")
    private String pubKeyPath;
    @Value("${atp.path.dabeUser}")
    private String userPath;

    private static final String NO_USER_ERROR = "no user for file:";

    public UserRepositoryServiceImpl(ChaincodeService chaincodeService,
                                     DABEService dabeService) {
        this.chaincodeService = chaincodeService;
        this.dabeService = dabeService;
    }

    @Override
    public ChaincodeResponse createUser(CreateUserRequest request) {
        DABEUser user = dabeService.getUser(request.getFileName());
        Preconditions.checkNotNull(user, NO_USER_ERROR + request.getFileName());
        Preconditions.checkNotNull(user.getName());
        Preconditions.checkNotNull(user.getEggAlpha());
        try {
            CommonController cc = new CommonController();
            cc.generateRsaKeysFile(user.getName());
            String priKey = FileUtils.readFileToString(new File(priKeyPath + request.getFileName()),
                StandardCharsets.UTF_8);
            String pubKey = FileUtils.readFileToString(new File(pubKeyPath + request.getFileName()),
                StandardCharsets.UTF_8);

            CreateUserCCRequest ccRequest =
                CreateUserCCRequest.builder()
                    .publicKey(pubKey)
                    .uid(user.getName())
                    .upk(user.getEggAlpha())
                    .userType(request.getUserType())
                    .build();
            CCUtils.sign(ccRequest, priKey);
            return chaincodeService.invoke(
                ChaincodeTypeEnum.TRUST_PLATFORM, "/user/create", ccRequest);
        } catch (IOException e) {
            throw new BaseException(e.getMessage());
        }
        //todo: apply certificate for user

    }

    @Override
    public ChaincodeResponse createUserInOne(String userName,String userType,String channel) {
        DABEUser user = dabeService.getUser(userName);
        Preconditions.checkNotNull(user, NO_USER_ERROR + userName);
        Preconditions.checkNotNull(user.getName());
        Preconditions.checkNotNull(user.getEggAlpha());
//        CommonController cc = new CommonController();
//        cc.generateRsaKeysFile(user.getName());
//        String priKey = FileUtils.readFileToString(new File(priKeyPath + userName),
//                StandardCharsets.UTF_8);
//        String pubKey = FileUtils.readFileToString(new File(pubKeyPath + userName),
//                StandardCharsets.UTF_8);
//        CreateUserCCRequest ccRequest =
//                CreateUserCCRequest.builder()
//                        .publicKey(pubKey)
//                        .uid(user.getName())
//                        .upk(user.getEggAlpha())
//                        .userType(userType)
//                        .channel(channel)
//                        .build();
//        return chaincodeService.invoke(
//                ChaincodeTypeEnum.TRUST_PLATFORM, "/user/create", ccRequest);
        try {
            CommonController cc = new CommonController();
            cc.generateRsaKeysFile(user.getName());
            String priKey = FileUtils.readFileToString(new File(priKeyPath + userName),
                    StandardCharsets.UTF_8);
            String pubKey = FileUtils.readFileToString(new File(pubKeyPath + userName),
                    StandardCharsets.UTF_8);

            CreateUserCCRequest ccRequest =
                    CreateUserCCRequest.builder()
                            .publicKey(pubKey)
                            .uid(user.getName())
                            .upk(user.getEggAlpha())
                            .userType(userType)
                            .build();
            CCUtils.sign(ccRequest, priKey);
            return chaincodeService.invoke(
                    ChaincodeTypeEnum.TRUST_PLATFORM, "/user/create", ccRequest);
        } catch (IOException e) {
            throw new BaseException(e.getMessage());
        }
    }


    @Override
    public PlatUser queryUser(QueryUserRequest request) {
        QueryUserCCRequest ccRequest =
            QueryUserCCRequest.builder()
                .uid(request.getUserName())
                .publicKey(request.getPubKey())
                .build();
        ChaincodeResponse response = chaincodeService.query(
            ChaincodeTypeEnum.TRUST_PLATFORM, "/user/getUser", ccRequest);
        if (response.isFailed()) {
            throw new BaseException("no user for request: " + request.getUserName() + request.getPubKey());
        }
        return JsonProviderHolder.JACKSON.parse(response.getMessage(), PlatUser.class);
    }
}
