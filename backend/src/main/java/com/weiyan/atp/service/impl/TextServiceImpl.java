package com.weiyan.atp.service.impl;

import com.google.common.base.Preconditions;
import com.weiyan.atp.constant.BaseException;
import com.weiyan.atp.constant.ChaincodeTypeEnum;
import com.weiyan.atp.data.bean.ChaincodeResponse;
import com.weiyan.atp.data.bean.DABEUser;
import com.weiyan.atp.data.request.chaincode.dabe.DecryptContentCCRequest;
import com.weiyan.atp.data.request.chaincode.dabe.DecryptTextCCRequest;
import com.weiyan.atp.data.request.chaincode.dabe.EncryptContentCCRequest;
import com.weiyan.atp.data.request.chaincode.dabe.EncryptTextCCRequest;
import com.weiyan.atp.data.request.chaincode.plat.ShareContentCCRequest;
import com.weiyan.atp.data.request.web.DecryptTextRequest;
import com.weiyan.atp.data.request.web.EncryptTextRequest;
import com.weiyan.atp.data.response.intergration.DecryptTextResponse;
import com.weiyan.atp.data.response.intergration.EncryptTextResponse;
import com.weiyan.atp.data.response.intergration.EncryptionResponse;
import com.weiyan.atp.service.*;
import com.weiyan.atp.utils.CCUtils;
import com.weiyan.atp.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @author BerryChen0w0
 * @date 2024-06-20
 */
// 参照ContentServiceImpl
@Service
@Slf4j
@Validated
public class TextServiceImpl implements TextService {
    private final ChaincodeService chaincodeService;
    private final AttrService attrService;
    private final UserRepositoryService userRepositoryService;
    private final OrgRepositoryServiceImpl orgRepositoryService;
    private final DABEService dabeService;

//    private final IpfsService ipfsService;

    @Value("${atp.path.privateKey}")
    private String priKeyPath;

    public TextServiceImpl(ChaincodeService chaincodeService, AttrService attrService,
                              UserRepositoryService userRepositoryService,
                              OrgRepositoryServiceImpl orgRepositoryService,
//                              IpfsServiceImpl ipfsService,
                              DABEService dabeService) {
        this.chaincodeService = chaincodeService;
        this.attrService = attrService;
        this.userRepositoryService = userRepositoryService;
        this.orgRepositoryService = orgRepositoryService;
        this.dabeService = dabeService;
//        this.ipfsService = ipfsService;
    }

    @Override
    public EncryptTextResponse encrypt(EncryptTextRequest request) {
        // 封装EncryptTextCCRequest，发送到链上调用DABE合约，得到加密结果作为返回值
        // 封装合约调用结果为EncryptTextResponse返回
        EncryptTextCCRequest ccRequest = EncryptTextCCRequest.builder()
                .plainText(request.getText())
                .policy(request.getPolicy())
                .userID(request.getUserID())
                .caseID(request.getCaseID())
                .fieldName(request.getText())
                .authorityMap(EncryptTextCCRequest.buildAuthorityMap(
                        request.getPolicy(), attrService, userRepositoryService, orgRepositoryService))
                .build();
        log.info("in TextServiceImpl.encrypt(): EncryptTextCCRequest: " + ccRequest);
        ChaincodeResponse ccResponse =
                chaincodeService.query(ChaincodeTypeEnum.DABE, "/common/encrypt", ccRequest);
        if (ccResponse.isFailed()) {
            log.error("TextService: [chaincode DABE] query for encrypt error: {}", ccResponse.getMessage());
            throw new BaseException("TextService: [chaincode DABE] query for encrypt error: " + ccResponse.getMessage());
        }

        return EncryptTextResponse.builder()
                .cipherText(ccResponse.getMessage())
                .build();
    }

    @Override
    public DecryptTextResponse decrypt(DecryptTextRequest request) {
        // 封装DecryptTextCCRequest，发送到链上调用DABE合约，得到解密结果作为返回值
        // 封装合约调用结果为DecryptTextResponse返回
        DecryptTextCCRequest ccRequest = DecryptTextCCRequest.builder()
                .cipherText(request.getCipherText())
                .userID(request.getUserID())
                .caseID(request.getCaseID())
                .fieldName(request.getFieldName())
                .attrMap(DecryptTextCCRequest.buildAttrMap(
                        request.getCipherText(), request.getUserID(), dabeService))
                .build();
        log.info("in TextServiceImpl.decrypt(): DecryptTextCCRequest: " + ccRequest);
        ChaincodeResponse ccResponse =
                chaincodeService.query(ChaincodeTypeEnum.DABE, "/common/decrypt", ccRequest);
        if (ccResponse.isFailed()) {
            if (!ccResponse.getMessage().startsWith("User attrs not satisfy the policy:")) {
                log.error("TextService: [chaincode DABE] query for decrypt error: {}", ccResponse.getMessage());
                throw new BaseException("TextService: [chaincode DABE] query for decrypt error: " + ccResponse.getMessage());
            } else {
                return DecryptTextResponse.builder().success(false).build();
            }
        }

        return DecryptTextResponse.builder()
//                .plainText(ccResponse.getMessage())
                .success(true)
                .build();
    }
}
