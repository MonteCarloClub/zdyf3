package com.weiyan.atp.service.impl;

import com.google.common.base.Preconditions;

import com.fasterxml.jackson.core.type.TypeReference;
import com.weiyan.atp.constant.BaseException;
import com.weiyan.atp.constant.ChaincodeTypeEnum;
import com.weiyan.atp.data.bean.ChaincodeResponse;
import com.weiyan.atp.data.bean.DABEUser;
import com.weiyan.atp.data.bean.PlatContent;
import com.weiyan.atp.data.request.chaincode.dabe.DecryptContentCCRequest;
import com.weiyan.atp.data.request.chaincode.dabe.EncryptContentCCRequest;
import com.weiyan.atp.data.request.chaincode.plat.QueryContentsCCRequest;
import com.weiyan.atp.data.request.chaincode.plat.ShareContentCCRequest;
import com.weiyan.atp.data.request.web.ShareContentRequest;
import com.weiyan.atp.data.request.web.UploadFileRequest;
import com.weiyan.atp.data.response.chaincode.plat.BaseListResponse;
import com.weiyan.atp.data.response.chaincode.plat.ContentResponse;
import com.weiyan.atp.data.response.intergration.EncryptionResponse;
import com.weiyan.atp.data.response.web.PlatContentsResponse;
import com.weiyan.atp.service.AttrService;
import com.weiyan.atp.service.ChaincodeService;
import com.weiyan.atp.service.ContentService;
import com.weiyan.atp.service.DABEService;
import com.weiyan.atp.service.UserRepositoryService;
import com.weiyan.atp.utils.CCUtils;
import com.weiyan.atp.utils.JsonProviderHolder;

import com.weiyan.atp.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.stream.Collectors;

import javax.validation.constraints.NotEmpty;

/**
 * @author : 魏延thor
 * @since : 2020/6/11
 */
@Service
@Slf4j
@Validated
public class ContentServiceImpl implements ContentService {
    private final ChaincodeService chaincodeService;
    private final AttrService attrService;
    private final UserRepositoryService userRepositoryService;
    private final OrgRepositoryServiceImpl orgRepositoryService;
    private final DABEService dabeService;

    @Value("${atp.path.privateKey}")
    private String priKeyPath;

    @Value("${atp.path.shareData}")
    private String shareDataPath;

    public ContentServiceImpl(ChaincodeService chaincodeService, AttrService attrService,
                              UserRepositoryService userRepositoryService,
                              OrgRepositoryServiceImpl orgRepositoryService,
                              DABEService dabeService) {
        this.chaincodeService = chaincodeService;
        this.attrService = attrService;
        this.userRepositoryService = userRepositoryService;
        this.orgRepositoryService = orgRepositoryService;
        this.dabeService = dabeService;
    }

    /**
     * 分享内容
     */
    @Override
    public void shareContent(ShareContentRequest request) {
        String encryptedContent = getEncryptedContent(request);
        DABEUser user = dabeService.getUser(request.getFileName());
        Preconditions.checkNotNull(user.getName());

        try {
            String priKey = FileUtils.readFileToString(
                new File(priKeyPath + request.getFileName()),
                StandardCharsets.UTF_8);

            ShareContentCCRequest shareContentCCRequest = ShareContentCCRequest.builder()
                .uid(user.getName())
                .tags(request.getTags())
                .content(encryptedContent)
                .build();
            CCUtils.sign(shareContentCCRequest, priKey);
            ChaincodeResponse response = chaincodeService.invoke(
                ChaincodeTypeEnum.TRUST_PLATFORM, "/common/shareMessage", shareContentCCRequest);
            if (response.isFailed()) {
                log.info("invoke share content to plat error: {}", response.getMessage());
                throw new BaseException("invoke share content to plat error");
            }
        } catch (IOException e) {
            log.info("get priKey", e);
            throw new BaseException(e.getMessage());
        }
        log.info("invoke share content to plat success");
    }

    /**
     * 系统对接 - 分享内容
     */
    @Override
    public EncryptionResponse encContent(ShareContentRequest request) {
        String encryptedContent = getEncryptedContent(request);
        DABEUser user = dabeService.getUser(request.getFileName());
        Preconditions.checkNotNull(user.getName());

        try {
            String priKey = FileUtils.readFileToString(
                    new File(priKeyPath + request.getFileName()),
                    StandardCharsets.UTF_8);

            ShareContentCCRequest shareContentCCRequest = ShareContentCCRequest.builder()
                    .uid(user.getName())
                    .tags(request.getTags())
                    //.content(encryptedContent)
                    .fileName(request.getSharedFileName())
                    .build();
            CCUtils.sign(shareContentCCRequest, priKey);
            ChaincodeResponse response = chaincodeService.invoke(
                    ChaincodeTypeEnum.TRUST_PLATFORM, "/common/shareMessage", shareContentCCRequest);
            if (response.isFailed()) {
                log.info("invoke share content to plat error: {}", response.getMessage());
                throw new BaseException("invoke share content to plat error");
            }
        } catch (IOException e) {
            log.info("get priKey", e);
            throw new BaseException(e.getMessage());
        }
        log.info("invoke share content to plat success");

        return EncryptionResponse.builder()
                .cipher(encryptedContent)
                .cipherHash(SecurityUtils.md5(encryptedContent))
                .policy(request.getPolicy())
                .tags(request.getTags())
                .uid(user.getName())
                .timeStamp(String.valueOf(System.currentTimeMillis()))
                .build();
    }

    @Override
    public EncryptionResponse encContent2(ShareContentRequest request) {
        String encryptedContent = getEncryptedContent(request);
        DABEUser user = dabeService.getUser(request.getFileName());
        Preconditions.checkNotNull(user.getName());
        try {
            String priKey = FileUtils.readFileToString(
                    new File(priKeyPath + request.getFileName()),
                    StandardCharsets.UTF_8);
        ShareContentCCRequest shareContentCCRequest = ShareContentCCRequest.builder()
                .uid(user.getName())
                .tags(request.getTags())
               // .content(encryptedContent)
                .timestamp(new Date().toString())
                .fileName(request.getSharedFileName())
                .ip(request.getIp())
                .location(request.getLocation())
                .policy(request.getPolicy())
                .build();
        System.out.println("ccccccccccccccccc");
        System.out.println(shareContentCCRequest.toString());
            CCUtils.sign(shareContentCCRequest, priKey);
        ChaincodeResponse response = chaincodeService.invoke(
                ChaincodeTypeEnum.TRUST_PLATFORM, "/common/shareMessage", shareContentCCRequest);
        if (response.isFailed()) {
            log.info("invoke share content to plat error: {}", response.getMessage());
            throw new BaseException("invoke share content to plat error");
        }
        } catch (IOException e) {
            log.info("get priKey", e);
            throw new BaseException(e.getMessage());
        }
        log.info("invoke share content to plat success");

        return EncryptionResponse.builder()
                .cipher(encryptedContent)
                .cipherHash(SecurityUtils.md5(encryptedContent))
                .policy(request.getPolicy())
                .tags(request.getTags())
                .uid(user.getName())
                .timeStamp(String.valueOf(System.currentTimeMillis()))
                .build();
    }


    @Override
    public String decryptContent(@NotEmpty String cipher, @NotEmpty String fileName) {
        DABEUser user = dabeService.getUser(fileName);
        DecryptContentCCRequest ccRequest = new DecryptContentCCRequest(cipher,"", "",user);
        ChaincodeResponse response = chaincodeService.query(
            ChaincodeTypeEnum.DABE, "/common/decrypt", ccRequest);
        if (response.isFailed()) {
            throw new BaseException("decryption error: " + response.getMessage());
        }
        return response.getMessage();
    }

    @Override
    public ChaincodeResponse decryptContent2(@NotEmpty String cipher, @NotEmpty String userName,  @NotEmpty String fileName,  @NotEmpty String sharedUser) {
        DABEUser user = dabeService.getUser(userName);
        DecryptContentCCRequest ccRequest = new DecryptContentCCRequest(cipher, fileName, sharedUser, user);
        return chaincodeService.query(
                ChaincodeTypeEnum.DABE, "/common/decrypt", ccRequest);
    }

    @Override
    public ChaincodeResponse getCipher(@NotEmpty String userName,  @NotEmpty String fileName,  @NotEmpty String sharedUser) {
        DABEUser user = dabeService.getUser(userName);
        DecryptContentCCRequest ccRequest = new DecryptContentCCRequest(userName,fileName,sharedUser);
        return chaincodeService.query(
                ChaincodeTypeEnum.DABE, "/common/getCipher", ccRequest);
    }

    @Override
    public PlatContentsResponse queryPlatContents(String fromUserName, String tag,
                                                  int pageSize, String bookmark) {
//        if (StringUtils.isEmpty(fromUserName) && StringUtils.isEmpty(tag)) {
//            throw new BaseException("request error, cannot query all message");
//        }
        System.out.println("nnnnnnnnnnnnnnnn");
        System.out.println(fromUserName);
        System.out.println(tag);
        System.out.println(bookmark);
        QueryContentsCCRequest request = QueryContentsCCRequest.builder()
            .fromUid(fromUserName)
            .tag(tag)
            .bookmark(bookmark)
            .pageSize(pageSize)
            .build();
        ChaincodeResponse response = chaincodeService.query(
            ChaincodeTypeEnum.TRUST_PLATFORM, "/common/getMessage", request);
        if (response.isFailed()) {
            log.info("query contents from plat error: {}", response.getMessage());
            throw new BaseException("query contents from plat error: " + response.getMessage());
        }
        System.out.println("response:");
        System.out.println(response.getMessage());
        BaseListResponse<ContentResponse> baseListResponse = JsonProviderHolder.JACKSON.parse(
            response.getMessage(), new TypeReference<BaseListResponse<ContentResponse>>() {});
        //System.out.println("baselistresponse:");
        //System.out.println(baseListResponse.getResult().stream());
        return PlatContentsResponse.builder()
            .bookmark(baseListResponse.getResponseMetadata().getBookmark())
            .count(Integer.parseInt(baseListResponse.getResponseMetadata().getRecordsCount()))
            .pageSize(pageSize)
            .contents(baseListResponse.getResult().stream()
                .map(contentResponseCCResult -> new PlatContent(contentResponseCCResult.getRecord()))
                .collect(Collectors.toList()))
            .build();

    }

    private String getEncryptedContent(ShareContentRequest request) {
        EncryptContentCCRequest ccRequest = EncryptContentCCRequest.builder()
            .plainContent(request.getPlainContent())
            .policy(request.getPolicy())
            .fileName(request.getSharedFileName())
            .userName(request.getFileName())
            .authorityMap(EncryptContentCCRequest.buildAuthorityMap(
                request.getPolicy(), attrService, userRepositoryService, orgRepositoryService))
            .build();

        ChaincodeResponse response =
            chaincodeService.query(ChaincodeTypeEnum.DABE, "/common/encrypt", ccRequest);
        if (response.isFailed()) {
            log.info("query for encrypt error: {}", response.getMessage());
            throw new BaseException("query for encrypt error: " + response.getMessage());
        }
        return response.getMessage();
    }
}
