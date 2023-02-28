package com.weiyan.atp.constant;

import lombok.Getter;

import java.util.Arrays;

/**
 * @author : 魏延thor
 * @since : 2020/6/3
 */
@Getter
public enum OrgApplyStatusEnum {
    PENDING_APPROVE(0),
    PENDING_SHARE(1),
    SUCCESS(2),
    FAIL(3),
    PENDING(4);

    private int iota;

    OrgApplyStatusEnum(int iota) {
        this.iota = iota;
    }

    public static OrgApplyStatusEnum getByInt(int i) {
        return Arrays.stream(OrgApplyStatusEnum.values())
            .filter(statusEnum -> statusEnum.iota == i)
            .findFirst()
            .orElseThrow(() -> new BaseException("no match OrgApplyStatusEnum"));
    }
}
