package com.weiyan.atp.constant;

import lombok.Getter;

import java.util.Arrays;

/**
 * @author : 魏延thor
 * @since : 2020/6/3
 */
@Getter
public enum ApplyTypeEnum {
    TO_USER(0),
    TO_ORG(1);

    private int iota;

    ApplyTypeEnum(int iota) {
        this.iota = iota;
    }

    public static ApplyTypeEnum getByInt(int i) {
        return Arrays.stream(ApplyTypeEnum.values())
            .filter(statusEnum -> statusEnum.iota == i)
            .findFirst()
            .orElseThrow(() -> new BaseException("no match AttrApplyStatusEnum"));
    }
}
