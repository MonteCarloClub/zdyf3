package com.weiyan.atp.data.bean.intergration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.weiyan.atp.data.bean.DABEUser;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Eric
 * @date 2021/11/9 10:40
 */
@Data
@JsonIgnoreProperties({"ASKMap","EGGAlpha","Alpha","GAlpha","OSKMap"})
public class Cert {
    @JsonProperty("APKMap")
    private Map<String, Cert.APK> apkMap;
    @JsonProperty("Name")
    private String name;
    @JsonProperty("OPKMap")
    private Map<String, Cert.OPKPart> opkMap;
    @JsonProperty("ExpireDate")
    private String expireDate;

    // 合约user没有的
    private Map<String, String> appliedAttrMap = new HashMap<>();
    // 使用对方公钥/属性密钥加密后的映射
    private Map<String, String> privacyAttrMap = new HashMap<>();

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class APK {
        @JsonProperty("Gy")
        private String gy;
    }


    @Data
    public static class OPKPart {
        @JsonProperty("OPK")
        private String opk;
        @JsonProperty("APKMap")
        private Map<String, String> apkMap;
    }
}
