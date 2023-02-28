package com.weiyan.atp.data.request.chaincode.dabe;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.weiyan.atp.data.bean.DABEUser;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author : 魏延thor
 * @since : 2020/6/11
 */
@Data
@Slf4j
public class DecryptContentCCRequest {
    @JsonProperty("Cipher")
    private String cipher;
    @JsonProperty("Uid")
    private String userName;
    @JsonProperty("AttrMap")
    private Map<String, String> attrMap;

    @JsonProperty("FileName")
    private String fileName;

    @JsonProperty("SharedUser")
    private String sharedUser;

    @JsonIgnore
    private static final String PATTERN = "[a-zA-Z0-9\u4e00-\u9f5a]+:[a-zA-Z0-9\u4e00-\u9fa5]+";
    //private static final String PATTERN = "[a-zA-Z0-9]+:[a-zA-Z0-9]+";

    public DecryptContentCCRequest(String cipher, String fileName, String sharedUser, DABEUser user) {
        Map<String, String> map = new HashMap<>();

        Matcher matcher = Pattern.compile(PATTERN).matcher(cipher);
        while (matcher.find()) {
            String attrName = matcher.group();
            map.put(attrName, user.getAppliedAttrMap().get(attrName));
        }

        this.cipher = cipher;
        this.userName = user.getName();
        this.attrMap = map;
        this.fileName = fileName;
        this.sharedUser = sharedUser;
    }

    public DecryptContentCCRequest(String userName, String fileName, String sharedUser) {
        this.cipher = "";
        this.userName = userName;
        this.attrMap = new HashMap<>();
        this.fileName = fileName;
        this.sharedUser = sharedUser;
    }
}
