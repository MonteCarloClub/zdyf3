package com.weiyan.atp.data.bean;

import lombok.Data;

import java.util.List;

/**
 * @author : 魏延thor
 * @since : 2020/6/8
 */
@Data
public class PlatUser {
    private String uid;
    private String publicKey;
    private List<String> attrSet;
    private List<String> holdAttrSet;
    private String upk;
}
