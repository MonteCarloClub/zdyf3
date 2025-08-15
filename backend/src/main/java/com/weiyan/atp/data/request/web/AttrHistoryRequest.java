package com.weiyan.atp.data.request.web;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

/**
 * @author : 魏延thor
 * @since : 2020/6/1
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttrHistoryRequest {
    private String userName;
}
