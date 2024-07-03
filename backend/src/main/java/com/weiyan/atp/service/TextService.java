package com.weiyan.atp.service;

import com.weiyan.atp.data.bean.ChaincodeResponse;
import com.weiyan.atp.data.request.web.DecryptTextRequest;
import com.weiyan.atp.data.request.web.EncryptTextRequest;
import com.weiyan.atp.data.response.intergration.DecryptTextResponse;
import com.weiyan.atp.data.response.intergration.EncryptTextResponse;
import com.weiyan.atp.data.response.intergration.EncryptionResponse;

/**
 * @author BerryChen0w0
 * @date 2024-06-20
 */
public interface TextService {

    EncryptTextResponse encrypt(EncryptTextRequest request);

    DecryptTextResponse decrypt(DecryptTextRequest request);
}
