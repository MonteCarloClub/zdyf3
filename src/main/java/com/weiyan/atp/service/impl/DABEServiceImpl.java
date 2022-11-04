package com.weiyan.atp.service.impl;

import com.weiyan.atp.constant.ChaincodeTypeEnum;
import com.weiyan.atp.data.bean.ChaincodeResponse;
import com.weiyan.atp.data.bean.DABEUser;
import com.weiyan.atp.service.ChaincodeService;
import com.weiyan.atp.service.DABEService;
import com.weiyan.atp.utils.CCUtils;
import com.weiyan.atp.utils.JsonProviderHolder;

import com.weiyan.atp.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import javax.validation.constraints.NotEmpty;

/**
 * @author : 魏延thor
 * @since : 2020/6/2
 */
@Service
@Slf4j
@Validated
public class DABEServiceImpl implements DABEService {
    private final ChaincodeService chaincodeService;
    @Value("${atp.path.dabeUser}")
    private String userPath;

    @Value("${atp.path.cert}")
    private String certPath;

    public DABEServiceImpl(ChaincodeService chaincodeService) {
        this.chaincodeService = chaincodeService;
    }

    @Override
    public DABEUser getUser(@NotEmpty String fileName) {
        try {
            String filePath = userPath + fileName;
            String resource = FileUtils.readFileToString(new File(filePath), StandardCharsets.UTF_8);
            return JsonProviderHolder.JACKSON.parse(resource, DABEUser.class);
        } catch (Exception e) {
            log.warn("get user error", e);
            return null;
        }
    }

    @Override
    public DABEUser getUser2(@NotEmpty String fileName, @NotEmpty String password) {
        try {
            String filePath = userPath + fileName;
            String resource = FileUtils.readFileToString(new File(filePath), StandardCharsets.UTF_8);
            DABEUser user = JsonProviderHolder.JACKSON.parse(resource, DABEUser.class);
            String hash = SecurityUtils.md5(password);
            if(!user.getPassword().equals(hash)){
                return null;
            }
            return user;
        } catch (Exception e) {
            log.warn("get user error", e);
            return null;
        }
    }

    @Override
    public DABEUser createUser(@NotEmpty String fileName, @NotEmpty String userName) {
        ChaincodeResponse response = chaincodeService.query(
            ChaincodeTypeEnum.DABE, "/user/create", new ArrayList<>(Collections.singletonList(userName)));
        CCUtils.saveResponse2(certPath, fileName, response);
        return CCUtils.saveResponse(userPath, fileName, response);
    }

    @Override
    public DABEUser createUser(@NotEmpty String fileName, @NotEmpty String userName, @NotEmpty String userType, @NotEmpty String channel, @NotEmpty String password) {
        ChaincodeResponse response = chaincodeService.query(
                ChaincodeTypeEnum.DABE, "/user/create", new ArrayList<>(Collections.singletonList(userName)));
        CCUtils.saveResponse2(certPath, fileName, response);
        return CCUtils.saveResponse(userPath, fileName, userType, channel, password,false, response);
    }

    @Override
    public DABEUser declareAttr(@NotEmpty String fileName, @NotEmpty String attrName) {
        DABEUser user = getUser(fileName);
        if (user == null) {
            log.info("no user found");
            return null;
        }
        String userJson = JsonProviderHolder.JACKSON.toJsonString(user);
        ChaincodeResponse response = chaincodeService.query(
            ChaincodeTypeEnum.DABE, "/user/declareAttr",
            new ArrayList<>(Arrays.asList(userJson, attrName)));
        return CCUtils.saveResponse(userPath, fileName,user.getUserType(),user.getChannel(),user.getPassword(),true, response);
    }

    @Override
    public ChaincodeResponse approveAttrApply(@NotEmpty String fileName, @NotEmpty String attrName,
                                              @NotEmpty String toUserName) {
        DABEUser user = getUser(fileName);
        if (user == null) {
            log.info("no user found");
            return null;
        }
        String userJson = JsonProviderHolder.JACKSON.toJsonString(user);
        return chaincodeService.query(ChaincodeTypeEnum.DABE, "/user/approveAttr",
            new ArrayList<>(Arrays.asList(userJson, toUserName, attrName)));
    }
}
