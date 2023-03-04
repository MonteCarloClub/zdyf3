package com.weiyan.atp.service.impl;

import com.google.common.base.Preconditions;

import com.weiyan.atp.constant.BaseException;
import com.weiyan.atp.constant.ChaincodeTypeEnum;
import com.weiyan.atp.constant.OrgApplyStatusEnum;
import com.weiyan.atp.constant.OrgApplyTypeEnum;
import com.weiyan.atp.data.bean.ChaincodeResponse;
import com.weiyan.atp.data.bean.DABEUser;
import com.weiyan.atp.data.bean.DABEUser.ASKPart;
import com.weiyan.atp.data.bean.DABEUser.OSKPart;
import com.weiyan.atp.data.bean.PlatOrg;
import com.weiyan.atp.data.bean.PlatOrgApply;
import com.weiyan.atp.data.bean.PlatUser;
import com.weiyan.atp.data.request.chaincode.plat.ApproveOrgApplyCCRequest;
import com.weiyan.atp.data.request.chaincode.plat.CreateOrgCCRequest;
import com.weiyan.atp.data.request.chaincode.plat.DeclareOrgAttrCCRequest;
import com.weiyan.atp.data.request.chaincode.plat.MixPartPKCCRequest;
import com.weiyan.atp.data.request.chaincode.plat.QueryOrgApplyCCRequest;
import com.weiyan.atp.data.request.chaincode.plat.SubmitOrgPartPKCCRequest;
import com.weiyan.atp.data.request.chaincode.plat.SubmitOrgShareCCRequest;
import com.weiyan.atp.data.request.web.ApproveOrgApplyRequest;
import com.weiyan.atp.data.request.web.CreateOrgRequest;
import com.weiyan.atp.data.request.web.DeclareOrgAttrRequest;
import com.weiyan.atp.data.request.web.QueryUserRequest;
import com.weiyan.atp.service.ChaincodeService;
import com.weiyan.atp.service.DABEService;
import com.weiyan.atp.service.OrgRepositoryService;
import com.weiyan.atp.service.UserRepositoryService;
import com.weiyan.atp.utils.CCUtils;
import com.weiyan.atp.utils.JsonProviderHolder;
import com.weiyan.atp.utils.SecurityUtils;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.util.encoders.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.validation.constraints.NotEmpty;

/**
 * @author : 魏延thor
 * @since : 2020/6/16
 */
@Service
@Slf4j
@Validated
public class OrgRepositoryServiceImpl implements OrgRepositoryService {
    private final ChaincodeService chaincodeService;
    private final DABEService dabeService;
    private final UserRepositoryService userRepositoryService;

    @Value("${atp.path.dabeUser}")
    private String userPath;
    @Value("${atp.path.privateKey}")
    private String priKeyPath;

    public OrgRepositoryServiceImpl(ChaincodeService chaincodeService, DABEService dabeService,
                                    UserRepositoryService userRepositoryService) {
        this.chaincodeService = chaincodeService;
        this.dabeService = dabeService;
        this.userRepositoryService = userRepositoryService;
    }

    @Override
    public ChaincodeResponse applyCreateOrg(CreateOrgRequest request) {
        DABEUser user = dabeService.getUser(request.getFileName());
        Preconditions.checkNotNull(user.getName());

        //check request
        if (request.getUsers().size() != request.getN()
            || request.getN() < request.getT()
            || request.getT() < 1
            || !request.getUsers().contains(user.getName())) {
            throw new BaseException("request error");
        }

        CreateOrgCCRequest ccRequest = CreateOrgCCRequest.builder()
            .t(request.getT())
            .n(request.getN())
            .orgId(request.getOrgName())
            .uidList(request.getUsers())
            .uid(user.getName())
            .userStr(JsonProviderHolder.JACKSON.toJsonString(user))
            .build();
        CCUtils.sign(ccRequest, getPriKey(request.getFileName()));
        return chaincodeService.invoke(
            ChaincodeTypeEnum.TRUST_PLATFORM, "/org/createOrgApply", ccRequest);
    }

    @Override
    public ChaincodeResponse applyCreateOrg2(CreateOrgRequest request) {
        DABEUser user = dabeService.getUser(request.getFileName());
        Preconditions.checkNotNull(user.getName());

        //check request
        if (request.getUsers().size() != request.getN()
                || request.getN() < request.getT()
                || request.getT() < 1
                || !request.getUsers().contains(user.getName())) {
            throw new BaseException("request error");
        }

        CreateOrgCCRequest ccRequest = CreateOrgCCRequest.builder()
                .t(request.getT())
                .n(request.getN())
                .orgId(request.getOrgName())
                .uidList(request.getUsers())
                .uid(user.getName())
                .userStr(JsonProviderHolder.JACKSON.toJsonString(user))
                .build();
        CCUtils.sign(ccRequest, getPriKey(request.getFileName()));
        return chaincodeService.invoke(
                ChaincodeTypeEnum.TRUST_PLATFORM, "/org/createOrgApply", ccRequest);
    }

    @Override
    public ChaincodeResponse applyDeclareOrgAttr(DeclareOrgAttrRequest request) {
        DABEUser user = dabeService.getUser(request.getFileName());
        Preconditions.checkNotNull(user.getName());

        DeclareOrgAttrCCRequest ccRequest = DeclareOrgAttrCCRequest.builder()
            .orgId(request.getOrgName())
            .attrName(request.getAttrName())
            .uid(user.getName())
            .build();
        CCUtils.sign(ccRequest, getPriKey(request.getFileName()));
        return chaincodeService.invoke(
            ChaincodeTypeEnum.TRUST_PLATFORM, "/org/declareAttrApply", ccRequest);
    }

    @Override
    public ChaincodeResponse applyDeclareOrgAttr2(DeclareOrgAttrRequest request) {
        DABEUser user = dabeService.getUser(request.getFileName());
        Preconditions.checkNotNull(user.getName());

        DeclareOrgAttrCCRequest ccRequest = DeclareOrgAttrCCRequest.builder()
                .orgId(request.getOrgName())
                .attrName(request.getAttrName())
                .uid(user.getName())
                .build();
        CCUtils.sign(ccRequest, getPriKey(request.getFileName()));
        return chaincodeService.invoke(
                ChaincodeTypeEnum.TRUST_PLATFORM, "/org/declareAttrApply", ccRequest);
    }

    public String getPriKey(String fileName) {
        try {
            return FileUtils.readFileToString(
                new File(priKeyPath + fileName),
                StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new BaseException("io error:" + e.getMessage());
        }
    }

    /**
     * 同意组织的申请
     * 0_5. 查询必要的申请信息
     * 1. plat的同意加入
     * 2. dabe生成对应的秘密
     * 3. plat提交秘密
     */
    @Override
    public void approveOrgApply(OrgApplyTypeEnum type,
                                ApproveOrgApplyRequest request) {
        DABEUser user = dabeService.getUser(request.getFileName());
        Preconditions.checkNotNull(user.getName());
        String priKey = getPriKey(request.getFileName());

        // 0_5. 查询必要的申请信息
        PlatOrgApply orgApply = queryOrgApply(request.getOrgName(), type, request.getAttrName());

        // 1. plat的同意加入
        if (!orgApply.getFromUserName().equals(user.getName())) {
            ApproveOrgApplyCCRequest ccRequest1 = ApproveOrgApplyCCRequest.builder()
                    .orgId(request.getOrgName())
                    .uid(user.getName())
                    .attrName(request.getAttrName())
                    .build();
            CCUtils.sign(ccRequest1, priKey);
            ChaincodeResponse response1 = chaincodeService.invoke(
                    ChaincodeTypeEnum.TRUST_PLATFORM, type.getApproveFunctionName(), ccRequest1);
            if (response1.isFailed()) {
                throw new BaseException("approve apply in plat error:" + response1.getMessage());
            }
        }

        // 2. dabe生成对应的秘密
        ArrayList<String> params = new ArrayList<>(Arrays.asList(
                JsonProviderHolder.JACKSON.toJsonString(user),
                request.getOrgName(),
                StringUtils.join(orgApply.getUidMap().keySet(), ","),
                orgApply.getT().toString(),
                orgApply.getN().toString()));
        if (StringUtils.isNotEmpty(request.getAttrName())) {
            params.add(request.getAttrName());
        }
        ChaincodeResponse response2 = chaincodeService.query(ChaincodeTypeEnum.DABE,
                "/user/share", params);
        if (response2.isFailed()) {
            throw new BaseException("generate share in dabe error: " + response2.getMessage());
        }

//        String filePath = userPath + fileName;
//        String resource = FileUtils.readFileToString(new File(filePath), StandardCharsets.UTF_8);
//        DABEUser user = JsonProviderHolder.JACKSON.parse(resource, DABEUser.class);
        DABEUser newUser = JsonProviderHolder.JACKSON.parse(response2.getMessage(), DABEUser.class);
        newUser.setPassword(user.getPassword());
        CCUtils.saveDABEUser(userPath + request.getFileName(),
                JsonProviderHolder.JACKSON.toJsonString(newUser));

        // 3. plat提交秘密
        Map<String, String> shareMap = type == OrgApplyTypeEnum.CREATION
                ? newUser.getOskMap().get(request.getOrgName()).getShare()
                : newUser.getOskMap().get(request.getOrgName()).getAskMap().get(request.getAttrName()).getShare();
        shareMap.entrySet()
                .stream()
                .filter(entry -> !entry.getKey().equals(user.getName()))
                .forEach(entry -> {
                    PlatUser platUser = userRepositoryService.queryUser(
                            QueryUserRequest.builder().userName(entry.getKey()).build());

                    SubmitOrgShareCCRequest ccRequest = SubmitOrgShareCCRequest.builder()
                            .orgId(request.getOrgName())
                            .share(new String(Base64.encode(
                                    SecurityUtils.encrypt(SecurityUtils.RSA_PKCS1,
                                            SecurityUtils.from(SecurityUtils.X509, platUser.getPublicKey()),
                                            entry.getValue().getBytes()))))
                            .toUid(entry.getKey())
                            .type(type)
                            .uid(user.getName())
                            .attrName(request.getAttrName())
                            .build();
                    CCUtils.sign(ccRequest, priKey);
                    ChaincodeResponse response = chaincodeService.invoke(
                            ChaincodeTypeEnum.TRUST_PLATFORM, "/org/shareSecret", ccRequest);
                    if (response.isFailed()) {
                        throw new BaseException("submit share to " + entry.getKey()
                                + " error: " + response.getMessage());
                    }
                });
    }

    @Override
    public void approveOrgApply2(OrgApplyTypeEnum type,
                                 ApproveOrgApplyRequest request) {
        DABEUser user = dabeService.getUser(request.getFileName());
        Preconditions.checkNotNull(user.getName());
        String priKey = getPriKey(request.getFileName());

        // 0_5. 查询必要的申请信息
        PlatOrgApply orgApply = queryOrgApply(request.getOrgName(), type, request.getAttrName());

        // 1. plat的同意加入
        if (!orgApply.getFromUserName().equals(user.getName())) {
            ApproveOrgApplyCCRequest ccRequest1 = ApproveOrgApplyCCRequest.builder()
                    .orgId(request.getOrgName())
                    .uid(user.getName())
                    .attrName(request.getAttrName())
                    .build();
            CCUtils.sign(ccRequest1, priKey);
            ChaincodeResponse response1 = chaincodeService.invoke(
                    ChaincodeTypeEnum.TRUST_PLATFORM, type.getApproveFunctionName(), ccRequest1);
            if (response1.isFailed()) {
                throw new BaseException("approve apply in plat error:" + response1.getMessage());
            }
        }

        // 2. dabe生成对应的秘密
        ArrayList<String> params = new ArrayList<>(Arrays.asList(
                JsonProviderHolder.JACKSON.toJsonString(user),
                request.getOrgName(),
                StringUtils.join(orgApply.getUidMap().keySet(), ","),
                orgApply.getT().toString(),
                orgApply.getN().toString()));
        if (StringUtils.isNotEmpty(request.getAttrName())) {
            params.add(request.getAttrName());
        }
        ChaincodeResponse response2 = chaincodeService.query(ChaincodeTypeEnum.DABE,
                "/user/share", params);
        if (response2.isFailed()) {
            throw new BaseException("generate share in dabe error: " + response2.getMessage());
        }
        DABEUser newUser = JsonProviderHolder.JACKSON.parse(response2.getMessage(), DABEUser.class);
        System.out.println("CHECKKKKK");
        newUser.setPassword(user.getPassword());
        newUser.setUserType(user.getUserType());
        newUser.setChannel(user.getChannel());
        CCUtils.saveDABEUser(userPath + request.getFileName(),
                JsonProviderHolder.JACKSON.toJsonString(newUser));
        System.out.println("checkkkkkkkkkkkkkkkkkk");
        System.out.println(JsonProviderHolder.JACKSON.toJsonString(user));
        System.out.println(JsonProviderHolder.JACKSON.toJsonString(newUser));


        // 3. plat提交秘密
        Map<String, String> shareMap = type == OrgApplyTypeEnum.CREATION
                ? newUser.getOskMap().get(request.getOrgName()).getShare()
                : newUser.getOskMap().get(request.getOrgName()).getAskMap().get(request.getAttrName()).getShare();
        shareMap.entrySet()
                .stream()
                .filter(entry -> !entry.getKey().equals(user.getName()))
                .forEach(entry -> {
                    PlatUser platUser = userRepositoryService.queryUser(
                            QueryUserRequest.builder().userName(entry.getKey()).build());

                    SubmitOrgShareCCRequest ccRequest = SubmitOrgShareCCRequest.builder()
                            .orgId(request.getOrgName())
                            .share(entry.getValue())
                            .toUid(entry.getKey())
                            .type(type)
                            .uid(user.getName())
                            .attrName(request.getAttrName())
                            .build();
                    CCUtils.sign(ccRequest, priKey);
                    ChaincodeResponse response = chaincodeService.invoke(
                            ChaincodeTypeEnum.TRUST_PLATFORM, "/org/shareSecret", ccRequest);
                    if (response.isFailed()) {
                        throw new BaseException("submit share to " + entry.getKey()
                                + " error: " + response.getMessage());
                    }
                });
    }

    /**
     * 提交part pk
     * 1. 获取其他人提供的秘密：检查是否能提交
     * 2. dabe 合并share
     * 3. plat 提交
     */
    @Override
    public void submitPartPk(OrgApplyTypeEnum type, String orgName,
                             String fileName, String attrName) {
        DABEUser user = dabeService.getUser(fileName);
        Preconditions.checkNotNull(user.getName());
        String priKey = getPriKey(fileName);
        PrivateKey privateKey = SecurityUtils.from(SecurityUtils.X509, priKey, "");

        if ((type == OrgApplyTypeEnum.CREATION && user.getOpkMap().containsKey(orgName)
            && StringUtils.isNotEmpty(user.getOpkMap().get(orgName).getOpk()))
            ||
            (type == OrgApplyTypeEnum.ATTRIBUTE && user.getOpkMap().containsKey(orgName)
                && user.getOpkMap().get(orgName).getApkMap().containsKey(attrName))) {
            //no need do anything
            log.info("already has part pk");
        } else {
            user = generatePartPk(type, orgName, fileName, attrName, user, privateKey);
        }

        // 3. plat 提交 /org/submitPartPK
        SubmitOrgPartPKCCRequest request = SubmitOrgPartPKCCRequest.builder()
            .orgId(orgName)
            .partPk(type == OrgApplyTypeEnum.CREATION ? user.getOpkMap().get(orgName).getOpk()
                : user.getOpkMap().get(orgName).getApkMap().get(attrName))
            .type(type)
            .uid(user.getName())
            .attrName(attrName)
            .build();
        CCUtils.sign(request, priKey);
        ChaincodeResponse response2 = chaincodeService.invoke(
            ChaincodeTypeEnum.TRUST_PLATFORM, "/org/submitPartPK", request);
        if (response2.isFailed()) {
            throw new BaseException("submit part pk in plat error:" + response2.getMessage());
        }
    }

    @Override
    public void submitPartPk2(OrgApplyTypeEnum type, String orgName,
                             String fileName, String attrName) {
        DABEUser user = dabeService.getUser(fileName);
        Preconditions.checkNotNull(user.getName());
        String priKey = getPriKey(fileName);
        PrivateKey privateKey = SecurityUtils.from(SecurityUtils.X509, priKey, "");
        if ((type == OrgApplyTypeEnum.CREATION && user.getOpkMap().containsKey(orgName)
                && StringUtils.isNotEmpty(user.getOpkMap().get(orgName).getOpk()))
                ||
                (type == OrgApplyTypeEnum.ATTRIBUTE && user.getOpkMap().containsKey(orgName)
                        && user.getOpkMap().get(orgName).getApkMap().containsKey(attrName))) {
            //no need do anything
            log.info("already has part pk");
        } else {
            user = generatePartPk(type, orgName, fileName, attrName, user, privateKey);
            System.out.println("0000000000000000000000000000");
            System.out.println(user);
//            user = generatePartPk2(type, orgName, fileName, attrName, user);
        }

        // 3. plat 提交 /org/submitPartPK
        SubmitOrgPartPKCCRequest request = SubmitOrgPartPKCCRequest.builder()
                .orgId(orgName)
                .partPk(type == OrgApplyTypeEnum.CREATION ? user.getOpkMap().get(orgName).getOpk()
                        : user.getOpkMap().get(orgName).getApkMap().get(attrName))
                .type(type)
                .uid(user.getName())
                .attrName(attrName)
                .build();
        CCUtils.sign(request, priKey);
        ChaincodeResponse response2 = chaincodeService.invoke(
                ChaincodeTypeEnum.TRUST_PLATFORM, "/org/submitPartPK", request);
        if (response2.isFailed()) {
            throw new BaseException("submit part pk in plat error:" + response2.getMessage());
        }
    }

    public DABEUser generatePartPk(OrgApplyTypeEnum type, String orgName, String fileName,
                                   String attrName, DABEUser user, PrivateKey privateKey) {
        // 1. 获取其他人提供的秘密：检查是否能提交
        PlatOrgApply orgApply = queryOrgApply(orgName, type, attrName);
        if (orgApply.getShareMap().getOrDefault(user.getName(), new HashMap<>())
                .size() != orgApply.getN() - 1) {
            throw new BaseException("share not enough");
        }
        if (!user.getOskMap().containsKey(orgName)) {
            throw new BaseException("need share secret first");
        }
//        orgApply.getShareMap().get(user.getName()).forEach((k, v) ->
//                orgApply.getShareMap().get(user.getName())
//                        .put(k, new String(SecurityUtils.decrypt(
//                                SecurityUtils.RSA_PKCS1, privateKey, Base64.decode(v)))));

        orgApply.getShareMap().get(user.getName()).forEach((k, v) ->
                orgApply.getShareMap().get(user.getName())
                        .put(k, v));
        if (type == OrgApplyTypeEnum.CREATION) {
            OSKPart oskPart = user.getOskMap().get(orgName);
            oskPart.getOthersShare().add(oskPart.getShare().get(user.getName()));
            oskPart.getOthersShare().addAll(orgApply.getShareMap().get(user.getName()).values());
        } else {
            ASKPart askPart = user.getOskMap().get(orgName).getAskMap().get(attrName);
            askPart.getOthersShare().add(askPart.getShare().get(user.getName()));
            askPart.getOthersShare().addAll(orgApply.getShareMap().get(user.getName()).values());
        }

        // 2. dabe 合并share
        ChaincodeResponse response = chaincodeService.query(
                ChaincodeTypeEnum.DABE, "/user/assembleShare",
                new ArrayList<>(Arrays.asList(
                        JsonProviderHolder.JACKSON.toJsonString(user),
                        orgName,
                        StringUtils.isEmpty(attrName) ? "" : attrName,
                        orgApply.getN().toString()
                )));
        if (response.isFailed()) {
            throw new BaseException("assemble share in dabe error: " + response.getMessage());
        }
        DABEUser newUser = JsonProviderHolder.JACKSON.parse(response.getMessage(), DABEUser.class);
        newUser.setPassword(user.getPassword());
        newUser.setUserType(user.getUserType());
        newUser.setChannel(user.getChannel());
        CCUtils.saveDABEUser(userPath + fileName,
                JsonProviderHolder.JACKSON.toJsonString(newUser));
//        DABEUser newUser = JsonProviderHolder.JACKSON.parse(response.getMessage(), DABEUser.class);
//        CCUtils.saveDABEUser(userPath + fileName,
//                JsonProviderHolder.JACKSON.toJsonString(newUser));
        return newUser;
    }

    public DABEUser generatePartPk2(OrgApplyTypeEnum type, String orgName, String fileName,
                                    String attrName, DABEUser user) {
        // 1. 获取其他人提供的秘密：检查是否能提交
        PlatOrgApply orgApply = queryOrgApply(orgName, type, attrName);
        if (orgApply.getShareMap().getOrDefault(user.getName(), new HashMap<>())
                .size() != orgApply.getN() - 1) {
            throw new BaseException("share not enough");
        }
        if (!user.getOskMap().containsKey(orgName)) {
            throw new BaseException("need share secret first");
        }
        orgApply.getShareMap().get(user.getName()).forEach((k, v) ->
                orgApply.getShareMap().get(user.getName())
                        .put(k, v));
        if (type == OrgApplyTypeEnum.CREATION) {
            OSKPart oskPart = user.getOskMap().get(orgName);
            oskPart.getOthersShare().add(oskPart.getShare().get(user.getName()));
            oskPart.getOthersShare().addAll(orgApply.getShareMap().get(user.getName()).values());
        } else {
            ASKPart askPart = user.getOskMap().get(orgName).getAskMap().get(attrName);
            askPart.getOthersShare().add(askPart.getShare().get(user.getName()));
            askPart.getOthersShare().addAll(orgApply.getShareMap().get(user.getName()).values());
        }

        // 2. dabe 合并share
        ChaincodeResponse response = chaincodeService.query(
                ChaincodeTypeEnum.DABE, "/user/assembleShare",
                new ArrayList<>(Arrays.asList(
                        JsonProviderHolder.JACKSON.toJsonString(user),
                        orgName,
                        StringUtils.isEmpty(attrName) ? "" : attrName,
                        orgApply.getN().toString()
                )));
        if (response.isFailed()) {
            throw new BaseException("assemble share in dabe error: " + response.getMessage());
        }
        DABEUser newUser = JsonProviderHolder.JACKSON.parse(response.getMessage(), DABEUser.class);
        newUser.setPassword(user.getPassword());
        newUser.setUserType(user.getUserType());
        newUser.setChannel(user.getChannel());
        CCUtils.saveDABEUser(userPath + fileName,
                JsonProviderHolder.JACKSON.toJsonString(newUser));
//        CCUtils.saveDABEUser(userPath + fileName,
//                JsonProviderHolder.JACKSON.toJsonString(newUser));
        return newUser;
    }

    @Override
    public void mixPartPk(OrgApplyTypeEnum type, String orgName, String attrName, String fileName) {
        DABEUser user = dabeService.getUser(fileName);
        Preconditions.checkNotNull(user.getName());
        String priKey = getPriKey(fileName);

        MixPartPKCCRequest request = MixPartPKCCRequest.builder()
            .orgId(orgName)
            .type(type)
            .uid(user.getName())
            .attrName(attrName)
            .build();
        CCUtils.sign(request, priKey);
        ChaincodeResponse response = chaincodeService.invoke(
            ChaincodeTypeEnum.TRUST_PLATFORM, "/org/mixPartPK", request);
        if (response.isFailed()) {
            throw new BaseException(response.getMessage());
        }
    }

    @Override
    public void mixPartPk2(OrgApplyTypeEnum type, String orgName, String attrName, String fileName) {
        DABEUser user = dabeService.getUser(fileName);
        Preconditions.checkNotNull(user.getName());
        String priKey = getPriKey(fileName);
        MixPartPKCCRequest request = MixPartPKCCRequest.builder()
                .orgId(orgName)
                .type(type)
                .uid(user.getName())
                .attrName(attrName)
                .build();
        CCUtils.sign(request, priKey);
        ChaincodeResponse response = chaincodeService.invoke(
                ChaincodeTypeEnum.TRUST_PLATFORM, "/org/mixPartPK", request);
        if (response.isFailed()) {
            throw new BaseException(response.getMessage());
        }
    }


    @Override
    public PlatOrg queryOrg(@NotEmpty String orgName) {
        ChaincodeResponse response = chaincodeService.query(
            ChaincodeTypeEnum.TRUST_PLATFORM, "/org/queryOrg",
            new ArrayList<>(Collections.singletonList(orgName)));
        if (response.isFailed()) {
            throw new BaseException("no org exists for " + orgName);
        }
        return JsonProviderHolder.JACKSON.parse(response.getMessage(), PlatOrg.class);
    }

    @Override
    public PlatOrgApply queryOrgApply(@NotEmpty String orgName, OrgApplyTypeEnum type, String attrName) {
        ChaincodeResponse response = chaincodeService.query(ChaincodeTypeEnum.TRUST_PLATFORM,
            type.getQueryFunctionName(),
            QueryOrgApplyCCRequest.builder()
                .orgId(orgName)
                .status(OrgApplyStatusEnum.PENDING.getIota())
                .attrName(attrName)
                .type(type)
                .build());
        if (response.isFailed()) {
            throw new BaseException("query cc error: " + response.getMessage());
        }
        return JsonProviderHolder.JACKSON.parse(response.getMessage(), PlatOrgApply.class);
    }
}
