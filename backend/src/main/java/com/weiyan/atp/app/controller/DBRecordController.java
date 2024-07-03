package com.weiyan.atp.app.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.weiyan.atp.data.bean.DBRecord;
import com.weiyan.atp.data.bean.Result;
import com.weiyan.atp.data.request.web.DecryptTextRequest;
import com.weiyan.atp.data.request.web.EncryptTextRequest;
import com.weiyan.atp.data.request.web.ShareContentRequest;
import com.weiyan.atp.data.response.intergration.DecryptTextResponse;
import com.weiyan.atp.data.response.intergration.EncryptTextResponse;
import com.weiyan.atp.data.response.intergration.EncryptionResponse;
import com.weiyan.atp.service.AttrService;
import com.weiyan.atp.service.ContentService;
import com.weiyan.atp.service.DABEService;
import com.weiyan.atp.service.TextService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author BerryChen0w0
 * @date 2024-06-20
 */
// 基于ContentController.java写的，主要参考其中的upload, cipher等函数
@RestController
@RequestMapping("/dbRecord")
@Slf4j
@CrossOrigin //支持跨域访问
public class DBRecordController {
//    private final ContentService contentService;
//    private final AttrService attrService;
//
//    private final DABEService dabeService;

    private final TextService textService;

//    @Value("${atp.devMode.baseUrl}")
//    private String baseUrl;

    @Value("${atp.path.dbRecordData}")
    private String dbRecordDataPath;

    public DBRecordController(TextService textService) {
        this.textService = textService;
    }

//    @Value("${atp.path.shareData}")
//    private String shareDataPath;
//
//    @Value("${atp.path.encryptData}")
//    private String encryptDataPath;
//
//    @Value("${atp.path.cipherData}")
//    private String cipherDataPath;
//
//    @Value("${atp.path.dabeUser}")
//    private String userPath;
//
//    @Value("${spring.datasource.url}")
//    private String mysqlUrl;
//
//    @Value("${spring.datasource.username}")
//    private String mysqlUser;
//
//    @Value("${spring.datasource.password}")
//    private String mysqlPassword;
//
//    @Value("${spring.datasource.driver-class-name}")
//    private String Driver;

//    private static int cnt = 0;
//
//    public DBRecordController(ContentService contentService, AttrService attrService, DABEService dabeService, TextService textService) {
//        this.contentService = contentService;
//        this.attrService = attrService;
//        this.dabeService = dabeService;
//        this.textService = textService;
//    }

    @PostMapping("/add")
//    public void addEntry(String caseID, String[] fields, String[] policies) throws IOException {
    public Result<Object> addDBRecord(String caseID, Map<String, String> policies, String userID) throws IOException {
        // userID是向系统中添加这条病例数据的用户（一般是系统管理员？）
        DBRecord dbRecord = new DBRecord(caseID, policies, userID);
        // 对case的每个字段(field)用其策略加密，密文存储到dbRecord.field.ct里
        for (DBRecord.DBField field : dbRecord.getFields()) {
            // 构建EncryptMessageCCRequest, 提交到链上进行加密，然后得到密文放到field.cipherText里
            EncryptTextRequest request = new EncryptTextRequest(field.getName(), field.getPolicy(), userID);
            EncryptTextResponse response = textService.encrypt(request);
            field.setCipherText(response.getCipherText());
        }
        log.info("addDBRecord: encrypted all fields in record: {}", dbRecord);

        // 把dbRecord转为json格式数据，写到文件里存储
        ObjectMapper mapper = new ObjectMapper();
        try {
            // dbRecord转json
            String json = mapper.writeValueAsString(dbRecord);
            log.info("addDBRecord: converted dbRecord to json: {}", json);
            // json写入文件
            File file = new File(new File(dbRecordDataPath).getAbsolutePath() +"/"+ caseID);
            FileUtils.writeStringToFile(file, json, StandardCharsets.UTF_8);    // 文件不存在时会自动创建
            log.info("addDBRecord: write record(json) to file: {}", file.getPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return Result.success();
    }

    @PostMapping("/query")
//    public List<String> queryDBRecord(String userID, String caseID) throws IOException {
    public Result<Object> queryDBRecord(String userID, String caseID) throws IOException {
        // 从文件名为caseID的文件中读出json内容，对每一个字段(field)的密文进行解密。返回解密成功的那些字段
        File file = new File(new File(dbRecordDataPath).getAbsolutePath() +"/"+ caseID);

        ObjectMapper mapper = new ObjectMapper();
        DBRecord dbRecord;
        try {
            dbRecord = mapper.readValue(file, DBRecord.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        log.info("queryDBRecord: read file to record: {}", dbRecord);

        List<String> accessibleFields = new ArrayList<>();
        for (DBRecord.DBField field : dbRecord.getFields()) {
            // 逐个解密，把能够解密的字段返回
            DecryptTextRequest request = new DecryptTextRequest(field.getCipherText(), userID);
            DecryptTextResponse response = textService.decrypt(request);
            if (response.getPlainText().equals(field.getName())) {
                accessibleFields.add(field.getName());
            }
        }

        return Result.okWithData(accessibleFields);
    }
}
