package com.weiyan.atp.data.request.web;

import lombok.Data;

@Data
public class SubmitPartPkRequest {
    private String type;
    private String orgName;
    private String fileName;
    private String attrName;
}
