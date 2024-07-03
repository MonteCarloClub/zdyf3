package com.weiyan.atp.data.response.intergration;

import lombok.Builder;
import lombok.Data;

/**
 * @author BerryChen0w0
 * @date 2024-06-20
 */
@Data
@Builder
public class EncryptTextResponse {
    private String cipherText;
}
