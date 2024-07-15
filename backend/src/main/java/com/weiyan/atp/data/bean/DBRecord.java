package com.weiyan.atp.data.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author BerryChen0w0
 * @date 2024-06-20
 */
// Database Record，数据库中的一条数据（对应华山医院的一个病例）
// 这里定义DBRecord类是为了存取和加解密数据方便
@Data
@NoArgsConstructor
public class DBRecord {
    @JsonProperty("CaseID")
    private String caseID;      // 这条数据的唯一标识
    @JsonProperty("Fields")
    private List<DBField> fields;   // 数据的所有字段（记录字段名、字段的访问控制策略、字段密文）
    @JsonProperty("OwnerID")
    private String ownerID;     // 这条数据的所有者（上传者）id

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DBField {
        // database field, 字段
        @JsonProperty("Name")
        private String name;
        @JsonProperty("Policy")
        private String policy;
        @JsonProperty("CipherText")
        private String cipherText;
    }

    public DBRecord(String caseID, Map<String, String> policies, String ownerID) {
        System.out.println("[br]:in DBRecord building function.");
        System.out.println("[br]:in DBRecord building function. caseID: "+caseID);
        System.out.println("[br]:in DBRecord building function. policies: "+policies);
        System.out.println("[br]:in DBRecord building function. ownerID: "+ownerID);
        this.caseID = caseID;
        this.fields = new ArrayList<>();
        for (String key : policies.keySet()) {
            DBField dbField = DBField.builder()
                    .name(key)
                    .policy(policies.get(key))
                    .build();
            this.fields.add(dbField);
        }
        this.ownerID = ownerID;
        System.out.println("[br]:in DBRecord building function. this DBRecord:" + this.toString());
    }

}