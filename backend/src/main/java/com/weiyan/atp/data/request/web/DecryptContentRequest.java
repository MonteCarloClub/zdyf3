package com.weiyan.atp.data.request.web;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author : 魏延thor
 * @since : 2020/10/29
 */
@Data
@NoArgsConstructor
public class DecryptContentRequest {
    @NotEmpty
    private String userName;

    @NotEmpty
    private String fileName;

    private List<String> tags;

    @NotEmpty
    private String sharedUser;

    private String ip;

    private String location;

    @Override
    public String toString() {
        return "DecryptContentRequest{" +
                "userName='" + userName + '\'' +
                ", fileName='" + fileName + '\'' +
                ", tags=" + tags +
                ", sharedUser='" + sharedUser + '\'' +
                ", ip='" + ip + '\'' +
                ", location='" + location + '\'' +
                '}';
    }
}
