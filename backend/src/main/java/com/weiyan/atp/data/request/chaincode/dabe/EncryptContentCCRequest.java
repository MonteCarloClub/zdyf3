package com.weiyan.atp.data.request.chaincode.dabe;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.weiyan.atp.constant.BaseException;
import com.weiyan.atp.data.bean.DABEUser.APK;
import com.weiyan.atp.data.bean.PlatAttr;
import com.weiyan.atp.data.bean.PlatOrg;
import com.weiyan.atp.data.bean.PlatUser;
import com.weiyan.atp.data.request.web.QueryUserRequest;
import com.weiyan.atp.service.AttrService;
import com.weiyan.atp.service.OrgRepositoryService;
import com.weiyan.atp.service.UserRepositoryService;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author : 魏延thor
 * @since : 2020/6/11
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class EncryptContentCCRequest {
    @JsonProperty("PlainContent")
    private String plainContent;
    @JsonProperty("Policy")
    private String policy;
    @JsonProperty("AuthorityMap")
    private Map<String, Authority> authorityMap;

    @JsonProperty("FileName")
    private String fileName;

    @JsonProperty("UserName")
    private String userName;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Authority {
        @JsonProperty("PK")
        private String pk;
        @JsonProperty("APKMap")
        private Map<String, APK> apkMap;
    }

    @JsonIgnore
    private static final String PATTERN = "[a-zA-Z0-9\u4e00-\u9f5a]+:[a-zA-Z0-9\u4e00-\u9fa5]+";
    //private static final String PATTERN = "[a-zA-Z0-9]+:[a-zA-Z0-9]+";

    public static Map<String, Authority> buildAuthorityMap(String policy, AttrService attrService,
                                                           UserRepositoryService userService,
                                                           OrgRepositoryService orgService) {
        Matcher matcher = Pattern.compile(PATTERN).matcher(policy);
        List<String> attrNames = new ArrayList<>();
        while (matcher.find()) {
            attrNames.add(matcher.group());
        }
        Map<String, String> attr2UserOrOrg = attrNames.stream()
            .collect(Collectors.toMap(
                attrName -> attrName,
                attrName -> StringUtils.split(attrName, ":")[0]));
        Map<String, Authority> authorityMap = attr2UserOrOrg.values()
            .stream()
            .distinct()
            .collect(Collectors.toMap(name -> name,
                name -> {
                    try {
                        PlatUser platUser = userService.queryUser(
                            QueryUserRequest.builder().userName(name).build());
                        if (StringUtils.isNotEmpty(platUser.getUid())) {
                            return Authority.builder()
                                .pk(platUser.getUpk())
                                .apkMap(new HashMap<>())
                                .build();
                        }
                    } catch (BaseException e) {
                        log.info("no user ", e);
                    }
                    PlatOrg platOrg = orgService.queryOrg(name);
                    return Authority.builder()
                        .pk(platOrg.getOpk())
                        .apkMap(new HashMap<>())
                        .build();
                }));
        attrNames.forEach(attrName -> {
            PlatAttr platAttr = attrService.queryAttrByName(attrName);
            authorityMap.get(attr2UserOrOrg.get(attrName)).getApkMap()
                .put(attrName, APK.builder().gy(platAttr.getApk()).build());
        });
        return authorityMap;
    }
}
