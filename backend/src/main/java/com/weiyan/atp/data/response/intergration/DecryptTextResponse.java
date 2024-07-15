package com.weiyan.atp.data.response.intergration;

import lombok.Builder;
import lombok.Data;

/**
 * @author BerryChen0w0
 * @date 2024-06-21
 */
@Data
@Builder
public class DecryptTextResponse {
//    private String plainText;
    // dabe.decrypt()并不返回明文，只是检查用户能否解密相关密文，如果能，返回的是shim.Success(nil)
    private boolean success = false;
}
