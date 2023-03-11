package com.weiyan.atp.data.request.chaincode.plat;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * @author : 魏延thor
 * @since : 2020/6/1
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@JsonPropertyOrder(alphabetic = true)
public class ShareContentCCRequest extends BaseCCRequest {
    private List<String> tags;
    /**
     * 加密后的cipher
     */
    private String content;

    private String fileName;

    private String ip;
    private String location;
    private String policy;

    @Override
    public String toString() {
        return "ShareContentCCRequest{" +
                "tags=" + tags +
                ", content='" + content + '\'' +
                ", fileName='" + fileName + '\'' +
                ", ip='" + ip + '\'' +
                ", location='" + location + '\'' +
                ", policy='" + policy + '\'' +
                '}';
    }
}
