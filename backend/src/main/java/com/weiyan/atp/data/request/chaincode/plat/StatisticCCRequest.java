package com.weiyan.atp.data.request.chaincode.plat;

import lombok.Data;
import lombok.experimental.SuperBuilder;

/**
 * @author Eric
 * @date 2022/1/3 20:10
 */
@Data
@SuperBuilder
public class StatisticCCRequest {
    private String uid;
}
