package com.weiyan.atp.data.request.web;

import lombok.Data;

import java.util.List;

import javax.validation.constraints.NotEmpty;

/**
 * @author : 魏延thor
 * @since : 2020/6/1
 */
@Data
public class QueryContentRequest {
    @NotEmpty
    private String fileName;

    private List<String> tags;

    private String plainContent;

    /**
     * (A AND B AND (C OR D))
     */
    private String policy;
}
