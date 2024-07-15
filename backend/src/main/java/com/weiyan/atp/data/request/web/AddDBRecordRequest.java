package com.weiyan.atp.data.request.web;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.Map;

/**
 * @author BerryChen0w0
 * @date 2024-07-15 14:02
 */
@Data
public class AddDBRecordRequest {
    @NotEmpty
    private String caseID;

    @NotEmpty
    private Map<String, String> policies;

    @NotEmpty
    private String userID;
}
