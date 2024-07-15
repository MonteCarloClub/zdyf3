package com.weiyan.atp.data.request.chaincode.dabe;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.weiyan.atp.data.bean.DABEUser;
import com.weiyan.atp.service.DABEService;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author BerryChen0w0
 * @date 2024-06-20
 */
// 参照DecryptContentCCRequest写的
@Data
@Builder
@Slf4j
public class DecryptTextCCRequest {
    //TODO:先写enc去了

    @JsonProperty("Cipher")     // 注：因为DABE合约里用的名字是Cipher，所以还是叫Cipher
    private String cipherText;
    @JsonProperty("Uid")    // 注：因为DABE合约里用的名字是Uid，所以还叫Uid
    private String userID;  // 发起解密请求的用户id
    @JsonProperty("AttrMap")
    private Map<String, String> attrMap;    // TODO：userID用户的属性列表？为什么是match(cipher)？密文里难道有属性？
    @JsonProperty("SharedUser")
    private String caseID;    // 密文的上传者。我们这里用caseId作为SharedUser
    @JsonProperty("FileName")
    private String fieldName;    // 密文文件名。我们这里用fieldName作为FileName

    @JsonIgnore
    private static final String PATTERN = "[a-zA-Z0-9\u4e00-\u9f5a]+:[a-zA-Z0-9\u4e00-\u9fa5]+";    // TODO：这里只支持大小写字母、数字和汉字。要支持下划线、符号等特殊字符的话，需要修改
    //private static final String PATTERN = "[a-zA-Z0-9]+:[a-zA-Z0-9]+";
//    private static final String PATTERN = "[\\w\u4e00-\u9f5a]+:[\\w\u4e00-\u9f5a]+";    // 匹配大小写字母、数字、下划线、基本汉字 TODO:还是改回去了，和属性申请那边一致，防止出奇怪的错误

    public static Map<String, String> buildAttrMap(String cipherText, String userID,
                                                   DABEService dabeService) {
        DABEUser user = dabeService.getUser(userID);
        Map<String, String> attrMap = new HashMap<>();

        Matcher matcher = Pattern.compile(PATTERN).matcher(cipherText);
        while (matcher.find()) {
            String attrName = matcher.group();
            attrMap.put(attrName, user.getAppliedAttrMap().get(attrName));
        }
        return attrMap;
    }

}
