package com.weiyan.atp.data.response.web;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author : 魏延thor
 * @since : 2020/6/1
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RsaKeysResponse {
    private String priKey;
    private String pubKey;
}
