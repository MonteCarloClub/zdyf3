//package com.weiyan.atp.service.impl;
//
//import com.weiyan.atp.data.request.web.DecryptTextRequest;
//import com.weiyan.atp.data.request.web.EncryptTextRequest;
//import com.weiyan.atp.data.response.intergration.DecryptTextResponse;
//import com.weiyan.atp.data.response.intergration.EncryptTextResponse;
//import com.weiyan.atp.service.TextService;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit4.SpringRunner;
//
//import javax.validation.constraints.NotEmpty;
//
///**
// * @author BerryChen0w0
// * @date 2024-07-15 11:13
// */
//
//// [br]目前参照IpfsServiceImplTest.java来写
//@RunWith(SpringRunner.class)
//@SpringBootTest
//public class TextServiceImplTest {
//    @Autowired
//    private TextService textService;
//
//    private String ct;      // 用于记录加密的密文，方便解密使用
//
//    @Test
//    public void testEncrypt() {
//        //TODO
//        String text = "FieldName1";    // 被要加密的数据
//        String policy = "(u1:u1a1 AND u1:u1a2)";  // 加密的属性策略
//        String userID = "u1";  // 发起加密请求的用户id
//        EncryptTextRequest encryptTextRequest = new EncryptTextRequest(text, policy, userID);
//        EncryptTextResponse response = textService.encrypt(encryptTextRequest);
//        this.ct = response.getCipherText();
//        System.out.println(response);
//    }
//
//    @Test
//    public void testDecrypt() {
//        // TODO
//        String cipherText = this.ct;    // 要解密的内容
//        String userID = "u2";  // 发起解密请求的用户id
//        DecryptTextRequest decryptTextRequest = new DecryptTextRequest(cipherText, userID);
//        DecryptTextResponse response = textService.decrypt(decryptTextRequest);
//        System.out.println(response);
//    }
//}
