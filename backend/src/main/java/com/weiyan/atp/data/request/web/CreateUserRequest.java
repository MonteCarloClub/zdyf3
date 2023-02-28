package com.weiyan.atp.data.request.web;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @author : 魏延thor
 * @since : 2020/6/1
 */
@Data
@AllArgsConstructor
public class CreateUserRequest {
    @NotEmpty
    private String fileName;

    @NotEmpty
    private String userType;
}
