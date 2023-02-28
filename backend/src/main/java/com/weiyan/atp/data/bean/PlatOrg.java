package com.weiyan.atp.data.bean;

import lombok.Data;

import java.util.List;

/**
 * @author : 魏延thor
 * @since : 2020/6/8
 */
@Data
public class PlatOrg {
    private String orgId;
    private List<String> uidSet;
    private List<String> attrSet;
    private Integer t;
    private Integer n;
    private String opk;
}
