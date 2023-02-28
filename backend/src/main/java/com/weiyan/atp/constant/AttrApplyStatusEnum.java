package com.weiyan.atp.constant;

import lombok.Getter;

import java.util.Arrays;

/**
 * @author : 魏延thor
 * @since : 2020/6/3
 */
@Getter
public enum AttrApplyStatusEnum {
    PENDING(0),
    SUCCESS(1),
    FAIL(2),
    REVOKE(3),
    ALL(4);

    private int iota;

    AttrApplyStatusEnum(int iota) {
        this.iota = iota;
    }

    public static AttrApplyStatusEnum getByInt(int i) {
        return Arrays.stream(AttrApplyStatusEnum.values())
            .filter(statusEnum -> statusEnum.iota == i)
            .findFirst()
            .orElseThrow(() -> new BaseException("no match AttrApplyStatusEnum"));
    }
}
