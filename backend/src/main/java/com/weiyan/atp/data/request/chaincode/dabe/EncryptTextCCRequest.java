package com.weiyan.atp.data.request.chaincode.dabe;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.weiyan.atp.constant.BaseException;
import com.weiyan.atp.data.bean.DABEUser;
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
 * @author BerryChen0w0
 * @date 2024-06-20
 */
// 参照EncryptContentCCRequest写的。去掉了fileName（因为我们不需要上传文件，没有文件名），把userName改成了userID，因为是请求者的用户id
// 检查了docker里的dabe合约代码，跟这个zdyf文件夹里的不一样！那个dabe的encrypt函数，也需要FileName和UserName的，
// 其中FileName是密文文件名（按照系统原本的设计，就是对应上传的文件的文件名）；UserName是用户名，即上传文件的用户
// 在我们华山医院项目的加密密文场景中，我们就改为，FileName是数据库的病例CaseID，UserName是该病例数据上传者的UserID
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class EncryptTextCCRequest {
    @JsonProperty("PlainContent")   // 注：因为DABE合约中用的是PlainContent，所以这里如果改名，则合约也要改了。所以还是叫PlainContent
    private String plainText;
    @JsonProperty("Policy")
    private String policy;
    @JsonProperty("AuthorityMap")
    private Map<String, Authority> authorityMap;
    @JsonProperty("UserID")
    private String userID;   // 发起请求的用户的id

    @JsonProperty("UserName")
    private String caseID;  // 病例id
    @JsonProperty("FileName")
    private String fieldName;    // 我们目前的设计里，就是把字段名作为明文进行加密，所以这个fieldName和plainText是一样的

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Authority {
        @JsonProperty("PK")
        private String pk;
        @JsonProperty("APKMap")
        private Map<String, DABEUser.APK> apkMap;
    }

    @JsonIgnore
    private static final String PATTERN = "[a-zA-Z0-9\u4e00-\u9f5a]+:[a-zA-Z0-9\u4e00-\u9fa5]+";
//    private static final String PATTERN = "[\\w\u4e00-\u9f5a]+:[\\w\u4e00-\u9f5a]+";    // 匹配大小写字母、数字、下划线、基本汉字
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
                    .put(attrName, DABEUser.APK.builder().gy(platAttr.getApk()).build());
        });
        return authorityMap;
    }
}