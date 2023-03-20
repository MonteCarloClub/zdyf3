package com.weiyan.atp.data.response.chaincode.plat;

import lombok.Data;

import java.util.List;

/**
 * @author : 魏延thor
 * @since : 2020/6/14
 */
@Data
public class ContentResponse {
    //private String content;
    private String fileName;
    private String uid;
    private List<String> tags;
    private String timeStamp;
    private String ip;
    private String policy;

    @Override
    public String toString() {
        return "ContentResponse{" +
                "fileName='" + fileName + '\'' +
                ", uid='" + uid + '\'' +
                ", tags=" + tags +
                ", timeStamp='" + timeStamp + '\'' +
                ", ip='" + ip + '\'' +
                ", policy='" + policy + '\'' +
                '}';
    }
}
