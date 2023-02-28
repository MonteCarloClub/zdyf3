package com.weiyan.atp.app.controller;

import com.weiyan.atp.constant.AttrApplyStatusEnum;
import com.weiyan.atp.constant.BaseException;
import com.weiyan.atp.constant.ChaincodeTypeEnum;
import com.weiyan.atp.data.bean.ChaincodeResponse;
import com.weiyan.atp.data.bean.ChannelInfo;
import com.weiyan.atp.data.bean.PlatUserAttrApply;
import com.weiyan.atp.data.bean.Result;
import com.weiyan.atp.data.request.chaincode.plat.QueryUserCCRequest;
import com.weiyan.atp.data.request.chaincode.plat.StatisticCCRequest;
import com.weiyan.atp.service.ChaincodeService;
import com.weiyan.atp.utils.JsonProviderHolder;
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
@RequestMapping("/statistic")
@CrossOrigin//支持跨域访问
public class StatisticController {
    private final ChaincodeService chaincodeService;

    public StatisticController(ChaincodeService chaincodeService) {
        this.chaincodeService = chaincodeService;
    }

    @GetMapping("/usercnt")
    public Result getUserCount(String userName) {
        ChaincodeResponse query = chaincodeService.query(ChaincodeTypeEnum.TRUST_PLATFORM, "/common/getUserCount", new ArrayList<>().add(userName));
        return query.getResult(str -> str);
    }

    @GetMapping("/attrcnt")
    public Result getAttrCount(String userName) {
        ChaincodeResponse query = chaincodeService.query(ChaincodeTypeEnum.TRUST_PLATFORM, "/common/attrCount",
                new ArrayList<>().add(userName));
        return query.getResult(str -> str);
    }

    @GetMapping("/orgcnt")
    public Result getOrgCount(String userName) {
        ChaincodeResponse query = chaincodeService.query(ChaincodeTypeEnum.TRUST_PLATFORM, "/common/getOrgCount",
                new ArrayList<>().add(userName));
        return query.getResult(str -> str);
    }

    @GetMapping("/channelcnt")
    public Result getChannelCount(String userName) {
        ChaincodeResponse query = chaincodeService.query(ChaincodeTypeEnum.TRUST_PLATFORM, "/common/getChannelCount",
                new ArrayList<>().add(userName));
        return query.getResult(str -> str);
    }

    @GetMapping("/channlelist")
    public Result<Object> queryAttrApply(String userName) {
        ChaincodeResponse query = chaincodeService.query(ChaincodeTypeEnum.TRUST_PLATFORM, "/common/getChannelList",
                new ArrayList<>().add(userName));
        return query.getResult(str -> JsonProviderHolder.JACKSON.parseList(str, ChannelInfo.class));

    }
}
