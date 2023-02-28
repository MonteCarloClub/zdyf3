package com.weiyan.atp.data.request.web;

import lombok.Data;

import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author : 魏延thor
 * @since : 2020/10/29
 */
@Data
public class DecryptContentRequest {
    @NotEmpty
    private String userName;

    @NotEmpty
    private String fileName;

    @NotEmpty
    private String cipher;

    private List<String> tags;

    @NotEmpty
    private String sharedUser;

    private String ip;

    private String location;
}
