package com.weiyan.atp.service;

import com.weiyan.atp.constant.ChaincodeTypeEnum;
import com.weiyan.atp.data.bean.ChaincodeResponse;

import java.util.List;

/**
 * @author : 魏延thor
 * @since : 2020/5/30
 */
public interface ChaincodeService {
    ChaincodeResponse invoke(ChaincodeTypeEnum ccType, String function, Object arg);

    ChaincodeResponse invoke(ChaincodeTypeEnum ccType, String function, List<String> args);

    ChaincodeResponse query(ChaincodeTypeEnum ccType, String function, Object arg);

    ChaincodeResponse query(ChaincodeTypeEnum ccType, String function, List<String> args);
}
