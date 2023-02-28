package com.weiyan.atp.app.controller;

import com.weiyan.atp.constant.ChaincodeTypeEnum;
import com.weiyan.atp.data.bean.ChaincodeResponse;
import com.weiyan.atp.data.bean.Result;
import com.weiyan.atp.data.request.chaincode.plat.QueryUserCCRequest;
import com.weiyan.atp.service.ChaincodeService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;

/**
 * @author : 魏延thor
 * @since : 2020/6/1
 */
@RestController
@Slf4j
@RequestMapping("/test")
@CrossOrigin //支持跨域访问
public class TestController {
    private final ChaincodeService chaincodeService;

    public TestController(ChaincodeService chaincodeService) {
        this.chaincodeService = chaincodeService;
    }

    @GetMapping("/user")
    public Result queryUser(String userName, String publicKey) {
        ChaincodeResponse query = chaincodeService.query(ChaincodeTypeEnum.TRUST_PLATFORM, "/user/getUser",
            QueryUserCCRequest.builder()
                .uid(userName)
                .publicKey(publicKey)
                .build());
        return Result.okWithData(query);
    }

    @GetMapping("/attr")
    public Result queryAttr(String attrName) {
        ChaincodeResponse query = chaincodeService.query(ChaincodeTypeEnum.TRUST_PLATFORM, "/common/getAttr",
            new ArrayList<>(Collections.singletonList(attrName)));
        return query.getResult(str -> str);
    }
}
