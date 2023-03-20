package com.weiyan.atp.service.impl;

import com.google.common.base.Preconditions;

import com.weiyan.atp.constant.AttrApplyStatusEnum;
import com.weiyan.atp.constant.BaseException;
import com.weiyan.atp.constant.ChaincodeTypeEnum;
import com.weiyan.atp.data.bean.ChaincodeResponse;
import com.weiyan.atp.data.bean.ChaincodeResponse.Status;
import com.weiyan.atp.data.bean.DABEUser;
import com.weiyan.atp.data.bean.PlatAttr;
import com.weiyan.atp.data.bean.PlatUser;
import com.weiyan.atp.data.bean.PlatUserAttrApply;
import com.weiyan.atp.data.request.chaincode.plat.*;
import com.weiyan.atp.data.request.web.*;
import com.weiyan.atp.service.AttrService;
import com.weiyan.atp.service.ChaincodeService;
import com.weiyan.atp.service.DABEService;
import com.weiyan.atp.service.UserRepositoryService;
import com.weiyan.atp.utils.CCUtils;
import com.weiyan.atp.utils.JsonProviderHolder;
import com.weiyan.atp.utils.SecurityUtils;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.bouncycastle.util.encoders.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * @author : 魏延thor
 * @since : 2020/6/11
 */
@Slf4j
@Service
public class AttrServiceImpl implements AttrService {
    private final ChaincodeService chaincodeService;
    private final DABEService dabeService;
    private final UserRepositoryService userRepositoryService;

    private static final String NO_USER_ERROR = "no user for file:";

    @Value("${atp.path.privateKey}")
    private String priKeyPath;
    @Value("${atp.path.publicKey}")
    private String pubKeyPath;
    @Value("${atp.path.dabeUser}")
    private String userPath;

    public AttrServiceImpl(ChaincodeService chaincodeService, DABEService dabeService,
                           UserRepositoryService userRepositoryService) {
        this.chaincodeService = chaincodeService;
        this.dabeService = dabeService;
        this.userRepositoryService = userRepositoryService;
    }

    @Override
    public PlatAttr queryAttrByName(String attrName) {
        ChaincodeResponse response = chaincodeService.query(ChaincodeTypeEnum.TRUST_PLATFORM,
            "/common/getAttr", new ArrayList<>(Collections.singletonList(attrName)));
        if (response.isFailed()) {
            throw new BaseException("no attr exists for " + attrName);
        }
        return JsonProviderHolder.JACKSON.parse(response.getMessage(), PlatAttr.class);
    }

    @Override
    public ChaincodeResponse declareUserAttr(DeclareUserAttrRequest request) {
        DABEUser user = dabeService.getUser(request.getFileName());
        Preconditions.checkNotNull(user, NO_USER_ERROR + request.getFileName());
        Preconditions.checkNotNull(user.getName());
        Preconditions.checkNotNull(user.getApkMap().get(request.getAttrName()), "no attr");

        try {
            String priKey = FileUtils.readFileToString(
                new File(priKeyPath + request.getFileName()),
                StandardCharsets.UTF_8);
            DeclareUserAttrCCRequest ccRequest =
                DeclareUserAttrCCRequest.builder()
                    .uid(user.getName())
                    .attrName(request.getAttrName().startsWith(user.getName())
                        ? request.getAttrName()
                        : user.getName() + ":" + request.getAttrName())
                    .apk(user.getApkMap().get(request.getAttrName()).getGy())
                    .build();
            CCUtils.sign(ccRequest, priKey);
            return chaincodeService.invoke(
                ChaincodeTypeEnum.TRUST_PLATFORM, "/user/declareAttr", ccRequest);
        } catch (IOException e) {
            throw new BaseException(e.getMessage());
        }
    }

    @Override
    public ChaincodeResponse declareUserAttr2(DeclareUserAttrRequest request) {
        DABEUser user = dabeService.getUser(request.getFileName());
        Preconditions.checkNotNull(user, NO_USER_ERROR + request.getFileName());
        Preconditions.checkNotNull(user.getName());
        System.out.println("uuuuuuuuuuuuuuuu");
        System.out.println(user.toString());
        Preconditions.checkNotNull(user.getApkMap().get(request.getAttrName()), "no attr");
        try{
        String priKey = FileUtils.readFileToString(new File(priKeyPath + request.getFileName()),
                StandardCharsets.UTF_8);
        DeclareUserAttrCCRequest ccRequest =
                DeclareUserAttrCCRequest.builder()
                        .uid(user.getName())
                        .attrName(request.getAttrName().startsWith(user.getName())
                                ? request.getAttrName()
                                : user.getName() + ":" + request.getAttrName())
                        .apk(user.getApkMap().get(request.getAttrName()).getGy())
                        .build();
        CCUtils.sign(ccRequest, priKey);
        return chaincodeService.invoke(
                ChaincodeTypeEnum.TRUST_PLATFORM, "/user/declareAttr", ccRequest);
        } catch (IOException e) {
            throw new BaseException(e.getMessage());
        }
    }

    @Override
    public ChaincodeResponse batchDeclareUserAttr(DeclareUserAttrRequest request) {
        DABEUser user = dabeService.getUser(request.getFileName());
        Preconditions.checkNotNull(user, NO_USER_ERROR + request.getFileName());
        Preconditions.checkNotNull(user.getName());
        Preconditions.checkNotNull(user.getApkMap().get(request.getAttrName()), "no attr");

        DeclareUserAttrCCRequest ccRequest =
                DeclareUserAttrCCRequest.builder()
                        .uid(user.getName())
                        .attrName(request.getAttrName().startsWith(user.getName())
                                ? request.getAttrName()
                                : user.getName() + ":" + request.getAttrName())
                        .apk(user.getApkMap().get(request.getAttrName()).getGy())
                        .build();
        return chaincodeService.invoke(
                ChaincodeTypeEnum.TRUST_PLATFORM, "/user/batchDeclareAttr", ccRequest);
    }

    /**
     * 申请属性
     * todo public为隐私的时候 对用户使用公钥加密，对组织使用组织属性加密，将加密后的密文放到文件的privacy map中
     */
    @Override
    public ChaincodeResponse applyAttr(ApplyUserAttrRequest request) {
        DABEUser user = dabeService.getUser(request.getFileName());
        Preconditions.checkNotNull(user, NO_USER_ERROR + request.getFileName());
        Preconditions.checkNotNull(user.getName());

        try {
            String priKey = FileUtils.readFileToString(
                new File(priKeyPath + request.getFileName()),
                StandardCharsets.UTF_8);
            ApplyUserAttrCCRequest ccRequest = ApplyUserAttrCCRequest.builder()
                .uid(user.getName())
                .toUid(request.getToUserName())
                .toOrgId(request.getToOrgName())
                .isPublic(request.getIsPublic())
                .attrName(request.getAttrName())
                .remark(request.getRemark())
                .build();
            CCUtils.sign(ccRequest, priKey);
            return chaincodeService.invoke(
                ChaincodeTypeEnum.TRUST_PLATFORM, "/user/applyAttr", ccRequest);
        } catch (IOException e) {
            throw new BaseException(e.getMessage());
        }
    }

    @Override
    public ChaincodeResponse applyAttr2(ApplyUserAttrRequest request) {
        System.out.println(request.toString());
        DABEUser user = dabeService.getUser(request.getFileName());
        Preconditions.checkNotNull(user, NO_USER_ERROR + request.getFileName());
        Preconditions.checkNotNull(user.getName());

//        ApplyUserAttrCCRequest ccRequest = ApplyUserAttrCCRequest.builder()
//                .uid(user.getName())
//                .toUid(request.getToUserName())
//                .toOrgId(request.getToOrgName())
//                .isPublic(true)
//                .attrName(request.getAttrName())
//                .remark(request.getRemark())
//                .build();
//        System.out.println("testtesttest");
//        System.out.println(ccRequest.toString());
//        return chaincodeService.invoke(
//                ChaincodeTypeEnum.TRUST_PLATFORM, "/user/applyAttr", ccRequest);
        try {
            String priKey = FileUtils.readFileToString(
                    new File(priKeyPath + request.getFileName()),
                    StandardCharsets.UTF_8);
            ApplyUserAttrCCRequest ccRequest = ApplyUserAttrCCRequest.builder()
                    .uid(user.getName())
                    .toUid(request.getToUserName())
                    .toOrgId(request.getToOrgName())
                    .isPublic(request.getIsPublic())
                    .attrName(request.getAttrName())
                    .remark(request.getRemark())
                    .build();
            CCUtils.sign(ccRequest, priKey);
            return chaincodeService.invoke(
                    ChaincodeTypeEnum.TRUST_PLATFORM, "/user/applyAttr", ccRequest);
        } catch (IOException e) {
            throw new BaseException(e.getMessage());
        }
    }

    //撤销他人属性
    @Override
    public ChaincodeResponse revokeAttr(RevokeUserAttrRequest request) {
        DABEUser user1 = dabeService.getUser(request.getUserName());
        Preconditions.checkNotNull(user1, NO_USER_ERROR + request.getUserName());
        Preconditions.checkNotNull(user1.getName());
        Preconditions.checkNotNull(user1.getApkMap().get(request.getAttrName()), "no attr");

        DABEUser user2 = dabeService.getUser(request.getToUserName());
        Preconditions.checkNotNull(user2, NO_USER_ERROR + request.getToUserName());
        Preconditions.checkNotNull(user2.getName());
        Preconditions.checkNotNull(user2.getAppliedAttrMap().get(request.getAttrName()), "no attr");

        user2.deleteAttr(request.getAttrName());

        CCUtils.saveDABEUser(userPath + user2.getName(),
                JsonProviderHolder.JACKSON.toJsonString(user2));

        RevokeUserAttrCCRequest ccRequest = RevokeUserAttrCCRequest.builder()
                .uid(user1.getName())
                .toUid(request.getToUserName())
                .attrName(request.getAttrName())
                .remark(request.getRemark())
                .build();
        return chaincodeService.invoke(
                ChaincodeTypeEnum.TRUST_PLATFORM, "/user/revokeAttr", ccRequest);
    }

    @Override
    public ChaincodeResponse batchApplyAttr(ApplyUserAttrRequest request) {
        DABEUser user = dabeService.getUser(request.getFileName());
        Preconditions.checkNotNull(user, NO_USER_ERROR + request.getFileName());
        Preconditions.checkNotNull(user.getName());

        try {
            String priKey = FileUtils.readFileToString(
                    new File(priKeyPath + request.getFileName()),
                    StandardCharsets.UTF_8);
            ApplyUserAttrCCRequest ccRequest = ApplyUserAttrCCRequest.builder()
                    .uid(user.getName())
                    .toUid(request.getToUserName())
                    .toOrgId(request.getToOrgName())
                    .isPublic(request.getIsPublic())
                    .attrName(request.getAttrName())
                    .remark(request.getRemark())
                    .build();
            CCUtils.sign(ccRequest, priKey);
            return chaincodeService.invoke(
                    ChaincodeTypeEnum.TRUST_PLATFORM, "/user/batchApplyAttr", ccRequest);
        } catch (IOException e) {
            throw new BaseException(e.getMessage());
        }
    }

    /**
     * todo 对结果中非公开的属性进行解密
     */
    @Override
    public ChaincodeResponse queryAttrApply(String toUid, String toOrgId,
                                            String userName, AttrApplyStatusEnum status) {
        System.out.println("ooooooooooooooooooooooooo");
        System.out.println(userName);
      //  userName = "深圳市气象局";
      //  status = AttrApplyStatusEnum.PENDING;
        QueryUserAttrApplyCCRequest ccRequest = QueryUserAttrApplyCCRequest.builder()
            .fromUid(userName)
            .toUid(toUid)
            .toOrgId(toOrgId)
            .status(status.getIota())
            .build();
        return chaincodeService.query(
            ChaincodeTypeEnum.TRUST_PLATFORM, "/user/getAttrApply", ccRequest);
    }

    /**
     * 查询属性历史
     */
    @Override
    public ChaincodeResponse queryAttrHistory(String uid) {
        QueryUserAttrHistoryCCRequest ccRequest = QueryUserAttrHistoryCCRequest.builder()
                .uid(uid)
                .build();
        return chaincodeService.query(
                ChaincodeTypeEnum.TRUST_PLATFORM, "/user/getAttrHistory", ccRequest);
    }

    /**
     * 审批属性申请
     */
    @Override
    public ChaincodeResponse approveAttrApply(ApproveAttrApplyRequest request) {
        DABEUser user = dabeService.getUser(request.getFileName());
        Preconditions.checkNotNull(user, NO_USER_ERROR + request.getFileName());
        Preconditions.checkNotNull(user.getName());

        try {
            // 自己签名私钥
            String priKey = FileUtils.readFileToString(
                new File(priKeyPath + request.getFileName()),
                StandardCharsets.UTF_8);

            // 检查属性名是否合法
            String[] split = StringUtils.split(request.getAttrName(), ":");
            if (split.length != 2) {
                throw new BaseException("attrName error");
            }

            // 如果同意需要给秘密
            String secret = null;
            if (request.getAgree().equals(Boolean.TRUE)) {
                ChaincodeResponse res1 = dabeService.approveAttrApply(
                    request.getFileName(), request.getAttrName(), request.getToUserName());
                if (res1.getStatus() == Status.FAIL) {
                    throw new BaseException("get secret failed");
                }
                PlatUser toUser = userRepositoryService.queryUser(
                    QueryUserRequest.builder().userName(request.getToUserName()).build());
                secret = new String(Base64.encode(
                    SecurityUtils.encrypt(
                        SecurityUtils.RSA_PKCS1,
                        SecurityUtils.from(SecurityUtils.X509, toUser.getPublicKey()),
                        res1.getMessage().getBytes())));
            }

            ApproveAttrApplyCCRequest ccRequest = ApproveAttrApplyCCRequest.builder()
                .uid(user.getName())
                .fromUid(request.getToUserName())
                .toOrgId(split[0].equals(user.getName()) ? "" : split[0])
                .attrName(request.getAttrName())
                .secret(secret)
                .agree(request.getAgree())
                .remark(request.getRemark())
                .build();
            CCUtils.sign(ccRequest, priKey);
            return chaincodeService.invoke(
                ChaincodeTypeEnum.TRUST_PLATFORM, "/user/approveAttrApply", ccRequest);
        } catch (IOException e) {
            throw new BaseException(e.getMessage());
        }
    }

    @Override
    public ChaincodeResponse approveAttrApply2(ApproveAttrApplyRequest request) {
        DABEUser user = dabeService.getUser(request.getFileName());
        Preconditions.checkNotNull(user, NO_USER_ERROR + request.getFileName());
        Preconditions.checkNotNull(user.getName());
        try{
            String priKey = FileUtils.readFileToString(
                    new File(priKeyPath + request.getFileName()),
                    StandardCharsets.UTF_8);

            // 检查属性名是否合法
            String[] split = StringUtils.split(request.getAttrName(), ":");
            if (split.length != 2) {
                throw new BaseException("attrName error");
            }

            // 如果同意需要给秘密
            String secret = null;
            if (request.getAgree().equals(Boolean.TRUE)) {
                ChaincodeResponse res1 = dabeService.approveAttrApply(
                        request.getFileName(), request.getAttrName(), request.getToUserName());
                if (res1.getStatus() == Status.FAIL) {
                    throw new BaseException("get secret failed");
                }
                secret = res1.getMessage();
            }

            ApproveAttrApplyCCRequest ccRequest = ApproveAttrApplyCCRequest.builder()
                    .uid(user.getName())
                    .fromUid(request.getToUserName())
                    .toOrgId(split[0].equals(user.getName()) ? "" : split[0])
                    .attrName(request.getAttrName())
                    .secret(secret)
                    .agree(request.getAgree())
                    .remark(request.getRemark())
                    .build();
            CCUtils.sign(ccRequest, priKey);
            return chaincodeService.invoke(
                    ChaincodeTypeEnum.TRUST_PLATFORM, "/user/approveAttrApply", ccRequest);
        }catch (IOException e) {
            throw new BaseException(e.getMessage());
        }

    }

    @Override
    public DABEUser syncSuccessAttrApply(String fileName) {
        return syncSuccessAttrApply2(fileName, "", "");
    }

    /**
     * 同步成功的属性申请
     */
    @Override
    public DABEUser syncSuccessAttrApply2(String fileName, String toUid, String toOrgId) {
        DABEUser user = dabeService.getUser(fileName);
        Preconditions.checkNotNull(user, NO_USER_ERROR + fileName);
        Preconditions.checkNotNull(user.getName());

        AtomicBoolean success = new AtomicBoolean(true);
        ChaincodeResponse response = null;
        try {
            String priKey = FileUtils.readFileToString(
                new File(priKeyPath + fileName),
                StandardCharsets.UTF_8);
            PrivateKey privateKey = SecurityUtils.from(SecurityUtils.X509, priKey, "");

            response = queryAttrApply(
                toUid, toOrgId, user.getName(), AttrApplyStatusEnum.SUCCESS);
            System.out.println("responsessssssssssssssssssssssssssss");
            System.out.println(response);
            if (response.getStatus() == Status.FAIL) {
                log.warn("query for attr apply failed");
                throw new BaseException("query for attr apply failed");
            }

            List<PlatUserAttrApply> successApplies = JsonProviderHolder.JACKSON.parseList(
                response.getMessage(), PlatUserAttrApply.class);
            System.out.println(successApplies);
            successApplies.stream()
                .filter(successApply ->
                    (successApply.getIsPublic().equals(Boolean.TRUE)
                        && !user.getAppliedAttrMap().containsKey(successApply.getAttrName()))
                        ||
                        (successApply.getIsPublic().equals(Boolean.FALSE)
                            && user.getPrivacyAttrMap().containsKey(successApply.getAttrName())
                            && !user.getAppliedAttrMap().containsKey(
                            user.getPrivacyAttrMap().get(successApply.getAttrName()))))
                .forEach(successApply -> {
                    try {
//                        String secret = getSecret(successApply, privateKey);
                        String secret = getSecret(successApply);
                        String plainAttrName = getPlainAttrName(successApply, user);
                        user.getAppliedAttrMap().put(plainAttrName, secret);
                    } catch (Exception e) {
                        success.set(false);
                        log.warn("syncSuccessAttrApply: sth wrong in loop", e);
                    }
                });
            CCUtils.saveDABEUser(userPath + fileName,
                JsonProviderHolder.JACKSON.toJsonString(user));
        } catch (Exception e) {
            log.error("syncSuccessAttrApply error", e);
            throw new BaseException(e.getMessage());
        }

        if (!success.get()) {
            throw new BaseException("sync sth wrong");
        }

        return user;
    }

    @Override
    public DABEUser syncSuccessAttrApply(String fileName, String toUid, String toOrgId) {
        DABEUser user = dabeService.getUser(fileName);
        Preconditions.checkNotNull(user, NO_USER_ERROR + fileName);
        Preconditions.checkNotNull(user.getName());
        user.setAppliedAttrMap(new HashMap<>());

        AtomicBoolean success = new AtomicBoolean(true);
        ChaincodeResponse response = null;
        try {
            response = queryAttrApply(
                    toUid, toOrgId, user.getName(), AttrApplyStatusEnum.SUCCESS);
            if (response.getStatus() == Status.FAIL) {
                log.warn("query for attr apply failed");
                throw new BaseException("query for attr apply failed");
            }

            List<PlatUserAttrApply> successApplies = JsonProviderHolder.JACKSON.parseList(
                    response.getMessage(), PlatUserAttrApply.class);
            System.out.println(successApplies);
            successApplies.stream()
                    .filter(successApply ->
                            (successApply.getIsPublic().equals(Boolean.TRUE)
                                    && !user.getAppliedAttrMap().containsKey(successApply.getAttrName()))
                                    ||
                                    (successApply.getIsPublic().equals(Boolean.FALSE)
                                            && user.getPrivacyAttrMap().containsKey(successApply.getAttrName())
                                            && !user.getAppliedAttrMap().containsKey(
                                            user.getPrivacyAttrMap().get(successApply.getAttrName()))))
                    .forEach(successApply -> {
                        try {
                            String secret = getSecret(successApply);
                            String plainAttrName = getPlainAttrName(successApply, user);
                            user.getAppliedAttrMap().put(plainAttrName, secret);
                        } catch (Exception e) {
                            success.set(false);
                            log.warn("syncSuccessAttrApply: sth wrong in loop", e);
                        }
                    });
            CCUtils.saveDABEUser(userPath + fileName,
                    JsonProviderHolder.JACKSON.toJsonString(user));
        } catch (Exception e) {
            log.error("syncSuccessAttrApply error", e);
            throw new BaseException(e.getMessage());
        }

        if (!success.get()) {
            throw new BaseException("sync sth wrong");
        }

        return user;
    }

    private String getPlainAttrName(PlatUserAttrApply successApply, DABEUser user) {
        return successApply.getIsPublic().equals(Boolean.TRUE)
            ? successApply.getAttrName()
            : user.getPrivacyAttrMap().get(successApply.getAttrName());
    }

    private String getSecret(PlatUserAttrApply successApply, PrivateKey privateKey) {
        if (successApply.getN() == 1) {
            // user attr
            System.out.println(successApply.getApprovalMap().get(successApply.getToUid()).getSecret());
            byte[] secret = SecurityUtils.decrypt(SecurityUtils.RSA_PKCS1, privateKey,
                Base64.decode(successApply.getApprovalMap().get(successApply.getToUid()).getSecret()));
            return new String(secret);
        } else {
            List<String> userNames = new ArrayList<>();
            List<String> secrets = new ArrayList<>();
            // org attr
            successApply.getApprovalMap().forEach((key, value) -> {
                userNames.add(key);
                if (value == null || value.getAgree().equals(Boolean.FALSE)) {
                    secrets.add("null");
                } else {
                    secrets.add(new String(SecurityUtils.decrypt(SecurityUtils.RSA_PKCS1,
                        privateKey, Base64.decode(value.getSecret()))));
                }
            });
            // query for assemble
            ChaincodeResponse response = chaincodeService.query(ChaincodeTypeEnum.DABE,
                "/user/assembleSecret",
                new ArrayList<>(Arrays.asList(
                    Strings.join(userNames, '-'), Strings.join(secrets, '-'),
                    String.valueOf(successApply.getT()), String.valueOf(successApply.getN()))));
            if (response.isFailed()) {
                throw new BaseException("query for assemble secret error:" + response.getMessage());
            }
            return response.getMessage();
        }
    }

    private String getSecret(PlatUserAttrApply successApply) {
        if (successApply.getN() == 1) {
            // user attr
            return successApply.getApprovalMap().get(successApply.getToUid()).getSecret();
        } else {
            List<String> userNames = new ArrayList<>();
            List<String> secrets = new ArrayList<>();
            // org attr
            successApply.getApprovalMap().forEach((key, value) -> {
                userNames.add(key);
                if (value == null || value.getAgree().equals(Boolean.FALSE)) {
                    secrets.add("null");
                } else {
                    secrets.add(value.getSecret());
                }
            });
            // query for assemble
            ChaincodeResponse response = chaincodeService.query(ChaincodeTypeEnum.DABE,
                    "/user/assembleSecret",
                    new ArrayList<>(Arrays.asList(
                            Strings.join(userNames, '-'), Strings.join(secrets, '-'),
                            String.valueOf(successApply.getT()), String.valueOf(successApply.getN()))));
            if (response.isFailed()) {
                throw new BaseException("query for assemble secret error:" + response.getMessage());
            }
            return response.getMessage();
        }
    }
}
