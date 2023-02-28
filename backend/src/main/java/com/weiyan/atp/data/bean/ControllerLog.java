package com.weiyan.atp.data.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ControllerLog {

    private String classMethod;

    private String ip;

    private String args;

    private String url;

    private String result;

    private long startTime;

    private long endTime;

    private long spendTime;

    private String httpMethod;

    private Object user;
}
