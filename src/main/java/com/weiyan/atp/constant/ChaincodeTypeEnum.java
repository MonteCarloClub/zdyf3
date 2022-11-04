package com.weiyan.atp.constant;

import lombok.Getter;

/**
 * @author : 魏延thor
 * @since : 2020/5/30
 */
@Getter
public enum ChaincodeTypeEnum {
    DABE("dabe"),
    TRUST_PLATFORM("plat");

    private String ccName;

    ChaincodeTypeEnum(String ccName) {
        this.ccName = ccName;
    }
}
