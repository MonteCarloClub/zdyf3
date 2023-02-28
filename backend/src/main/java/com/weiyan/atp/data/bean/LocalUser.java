package com.weiyan.atp.data.bean;

import org.apache.commons.io.IOUtils;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.User;
import org.hyperledger.fabric.sdk.exception.CryptoException;
import org.hyperledger.fabric.sdk.identity.X509Enrollment;
import org.hyperledger.fabric.sdk.security.CryptoPrimitives;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.PrivateKey;
import java.util.Set;

public class LocalUser implements User {

    private String name;
    private String mspId;
    private Enrollment enrollment;

    public LocalUser(String name, String mspId, String key, String cert) throws ClassNotFoundException, IOException, InstantiationException, CryptoException, IllegalAccessException {
        this.name = name;
        this.mspId = mspId;
        this.enrollment = loadFromPemFile(key, cert);
    }

    private Enrollment loadFromPemFile(String keyFile,String certFile) throws IllegalAccessException, InstantiationException, ClassNotFoundException, IOException, CryptoException { /*见下文说明*/
        InputStream keyIS = this.getClass().getResourceAsStream("/msp/keystore/key.pem");
        byte[] keyPem=IOUtils.toByteArray(keyIS);
        InputStream certIS = this.getClass().getResourceAsStream("/msp/signcerts/peer.pem");
        byte[] certPem =IOUtils.toByteArray(certIS);
//        byte[] keyPem = Files.readAllBytes(Paths.get(keyFile));     //载入私钥PEM文本
//        byte[] certPem = Files.readAllBytes(Paths.get(certFile));   //载入证书PEM文本
        CryptoPrimitives suite = new CryptoPrimitives();            //载入密码学套件
        PrivateKey privateKey = suite.bytesToPrivateKey(keyPem);    //将PEM文本转换为私钥对象
        return new X509Enrollment(privateKey,new String(certPem));  //创建并返回X509Enrollment对象
    }

    @Override public String getName(){ return name; }

    @Override
    public Set<String> getRoles() {
        return null;
    }

    @Override public String getMspId() { return mspId;}
    @Override public Enrollment getEnrollment() { return enrollment; }
    @Override public String getAccount() { return null; }
    @Override public String getAffiliation() { return null; }
}
