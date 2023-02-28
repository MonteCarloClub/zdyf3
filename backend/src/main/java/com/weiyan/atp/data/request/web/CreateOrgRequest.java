package com.weiyan.atp.data.request.web;

import lombok.Data;

import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author : 魏延thor
 * @since : 2020/6/16
 */
@Data
public class CreateOrgRequest {
    @NotEmpty
    private String fileName;
    @NotNull
    private Integer t;
    @NotNull
    private Integer n;

    private List<String> users;

    @NotEmpty
    private String orgName;
}
