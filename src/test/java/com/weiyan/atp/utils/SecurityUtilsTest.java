package com.weiyan.atp.utils;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.weiyan.atp.data.request.chaincode.plat.BaseCCRequest;
import com.weiyan.atp.data.response.web.RsaKeysResponse;

import org.bouncycastle.util.encoders.Base64;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * @author : 魏延thor
 * @since : 2020/6/9
 */
class SecurityUtilsTest {
    @BeforeEach
    void setUp() {
    }

    @Test
    void testEncryptAndDecrypt() throws NoSuchAlgorithmException {
        RsaKeysResponse rsaKeysResponse = SecurityUtils.generateKeyPair();
        PublicKey publicKey = SecurityUtils.from(SecurityUtils.X509, rsaKeysResponse.getPubKey());
        PrivateKey privateKey = SecurityUtils.from(SecurityUtils.X509, rsaKeysResponse.getPriKey(), "");

        byte[] cipherText = SecurityUtils.encrypt(SecurityUtils.RSA_PKCS1, publicKey, "testData".getBytes());
        System.out.println(new String(Base64.encode(cipherText)));
        assertArrayEquals(cipherText, Base64.decode(new String(Base64.encode(cipherText))));

        byte[] decrypt = SecurityUtils.decrypt(SecurityUtils.RSA_PKCS1, privateKey, cipherText);
        assertEquals("testData", new String(decrypt));
    }

    @Test
    void testSign() throws NoSuchAlgorithmException {
        RsaKeysResponse rsaKeysResponse = SecurityUtils.generateKeyPair();
        PublicKey publicKey = SecurityUtils.from(SecurityUtils.X509, rsaKeysResponse.getPubKey());
        PrivateKey privateKey = SecurityUtils.from(SecurityUtils.X509, rsaKeysResponse.getPriKey(), "");
        System.out.println(new String(Base64.encode(privateKey.getEncoded())));
        System.out.println(new String(Base64.encode(publicKey.getEncoded())));

        BaseCCRequest request = BaseCCRequest.builder().build();
        CCUtils.sign(request, new String(Base64.encode(privateKey.getEncoded())));
        System.out.println(request.getSign());
    }

    @Test
    void testHash(){
        String fileName="test.txt";
        String fileName2="test.tx";
        String hash1 = SecurityUtils.md5(fileName);
        String hash2 = SecurityUtils.md5(fileName2);
        System.out.println(hash1);
        System.out.println(hash2);
    }
}