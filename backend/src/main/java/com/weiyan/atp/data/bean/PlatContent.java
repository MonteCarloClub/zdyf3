package com.weiyan.atp.data.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.weiyan.atp.data.response.chaincode.plat.ContentResponse;

import com.weiyan.atp.utils.SecurityUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author : 魏延thor
 * @since : 2020/6/11
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlatContent {
    private String fileName;

    private String policy;

    private String cipher;

    private List<String> tags;

    //分享者
    private String sharedUser;

    private String timeStamp;

    private String ip;

    @JsonIgnore
    private static final String PATTERN = "Policy\":\"(.*?)\"";

    public PlatContent(ContentResponse response) {
        System.out.println("content response");
        System.out.println(response.toString());
        this.tags = response.getTags();
        this.fileName = response.getFileName();
        this.sharedUser = response.getUid();
        this.timeStamp = response.getTimestamp();
        System.out.println("tttttttttttt");
        System.out.println(this.timeStamp);
        this.ip = response.getIp();
        this.policy = response.getPolicy();
        try {
            this.cipher= FileUtils.readFileToString(
                    new File("atp/data/enc/" + response.getUid() + "/" + response.getFileName()),
                    StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(cipher!=null){
//            Matcher matcher = Pattern.compile(PATTERN).matcher(cipher);
//            if (matcher.find() && matcher.groupCount() == 1) {
//                this.policy = matcher.group(1);
//            }
            this.cipher = SecurityUtils.md5(this.cipher);
        }

    }
}
