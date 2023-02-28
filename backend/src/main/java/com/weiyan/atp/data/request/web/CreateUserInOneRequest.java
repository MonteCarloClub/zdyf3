package com.weiyan.atp.data.request.web;

import lombok.Data;

import javax.validation.constraints.NotEmpty;


@Data
public class CreateUserInOneRequest {
    @NotEmpty
    private String userName;

    @NotEmpty
    private String userType;

    @NotEmpty
    private String channel;

    @NotEmpty
    private String password;
}
