package com.weiyan.atp.data.request.web;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @author BerryChen0w0
 * @date 2024-07-15 14:07
 */
@Data
public class QueryDBRecordRequest {
    @NotEmpty
    private String userID;

    @NotEmpty
    private String caseID;
}
