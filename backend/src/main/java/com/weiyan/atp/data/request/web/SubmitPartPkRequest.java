package com.weiyan.atp.data.request.web;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubmitPartPkRequest {
    private String type;
    private String orgName;
    private String fileName;
    private String attrName;
}
