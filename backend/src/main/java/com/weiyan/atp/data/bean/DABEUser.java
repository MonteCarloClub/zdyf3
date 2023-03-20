package com.weiyan.atp.data.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author : 魏延thor
 * @since : 2020/6/2
 */
@Data
public class DABEUser {
    @JsonProperty("APKMap")
    private Map<String, APK> apkMap;
    @JsonProperty("ASKMap")
    private Map<String, ASK> askMap;
    @JsonProperty("EGGAlpha")
    private String eggAlpha;
    @JsonProperty("Alpha")
    private String alpha;
    @JsonProperty("GAlpha")
    private String gAlpha;
    @JsonProperty("Name")
    private String name;
    @JsonProperty("OPKMap")
    private Map<String, OPKPart> opkMap;
    @JsonProperty("OSKMap")
    private Map<String, OSKPart> oskMap;

    // 合约user没有的
    private Map<String, String> appliedAttrMap = new HashMap<>();

    // 使用对方公钥/属性密钥加密后的映射
    private Map<String, String> privacyAttrMap = new HashMap<>();

    public void deleteAttr(String key){
        appliedAttrMap.remove(key);
    }
    @JsonProperty("Password")
    private String password;

    @JsonProperty("UserType")
    private String userType;

    @JsonProperty("Channel")
    private String channel;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class APK {
        @JsonProperty("Gy")
        private String gy;
    }

    @Data
    public static class ASK {
        @JsonProperty("Y")
        private String y;
    }

    @Data
    public static class OPKPart {
        @JsonProperty("OPK")
        private String opk;
        @JsonProperty("APKMap")
        private Map<String, String> apkMap;
    }

    @Data
    public static class OSKPart {
        @JsonProperty("AlphaPart")
        private String alphaPart;
        @JsonProperty("ASKMap")
        private Map<String, ASKPart> askMap;
        @JsonProperty("F")
        private List<String> f;
        @JsonProperty("N")
        private int n;
        @JsonProperty("T")
        private int t;
        @JsonProperty("OthersShare")
        private List<String> othersShare;
        @JsonProperty("OSK")
        private String osk;
        @JsonProperty("GOSK")
        private String gOSK;
        @JsonProperty("Share")
        private Map<String, String> share;
    }

    @Data
    public static class ASKPart {
        @JsonProperty("F")
        private List<String> f;
        @JsonProperty("OthersShare")
        private List<String> othersShare;
        @JsonProperty("YPart")
        private String yPart;
        @JsonProperty("ASK")
        private String ask;
        @JsonProperty("Share")
        private Map<String, String> share;
    }
}
