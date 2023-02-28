package com.weiyan.atp.utils;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import com.weiyan.atp.constant.BaseException;
import com.weiyan.atp.data.response.web.RsaKeysResponse;

import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Enumeration;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.servlet.http.HttpServletRequest;

/**
 * @author : 魏延thor
 * @since : 2020/6/3
 */
public class SecurityUtils {
    private SecurityUtils() {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    }

    public static final String RSA = "RSA";
    public static final String RSA_PKCS1 = "RSA/ECB/PKCS1Padding";
    private static final String DEFAULT_PROVIDER = BouncyCastleProvider.PROVIDER_NAME;
    private static final String ERROR_ENCRYPT = "Error encrypt the data with key";
    private static final String ERROR_DECRYPT = "Error decrypt the data with key";
    public static final String X509 = "X.509";

    public static RsaKeysResponse generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator gen = KeyPairGenerator.getInstance(RSA);
        gen.initialize(2048);
        KeyPair keyPair = gen.generateKeyPair();
        PublicKey pubKey = keyPair.getPublic();
        PrivateKey priKey = keyPair.getPrivate();
        String pubEncBase64 = Base64.encodeBase64String(pubKey.getEncoded());
        String priEncBase64 = Base64.encodeBase64String(priKey.getEncoded());
        return RsaKeysResponse.builder()
            .priKey(priEncBase64)
            .pubKey(pubEncBase64)
            .build();
    }

    /**
     * Encrypts data bytes with the specified algorithm and {@code Key} instance.
     *
     * @param algorithm the algorithm
     * @param key       the {@code key}
     * @param data      the raw data bytes
     * @return the encrypted data bytes
     * @ if failed to encrypt data
     */
    public static byte[] encrypt(String algorithm, Key key, byte[] data) {
        Preconditions.checkNotNull(algorithm);
        Preconditions.checkNotNull(key);
        Preconditions.checkNotNull(data);

        try {
            Cipher encryptor = Cipher.getInstance(algorithm, DEFAULT_PROVIDER);
            encryptor.init(Cipher.ENCRYPT_MODE, key);
            return encryptor.doFinal(data);
        } catch (NoSuchProviderException | NoSuchAlgorithmException | InvalidKeyException
            | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException err) {
            throw new BaseException(ERROR_ENCRYPT, err);
        }
    }

    /**
     * Decrypts data bytes asymmetrically with the specified algorithm and {@code Key} instance.
     *
     * @param algorithm     the algorithm
     * @param key           the {@code key} instance
     * @param encryptedData the encrypted data bytes
     * @return the decrypted data bytes
     * @ if failed to decrypt data
     */
    public static byte[] decrypt(String algorithm, Key key, byte[] encryptedData) {
        Preconditions.checkNotNull(algorithm);
        Preconditions.checkNotNull(key);
        Preconditions.checkNotNull(encryptedData);

        try {
            Cipher decryptor = Cipher.getInstance(algorithm, DEFAULT_PROVIDER);
            decryptor.init(Cipher.DECRYPT_MODE, key);
            return decryptor.doFinal(encryptedData);
        } catch (NoSuchProviderException | NoSuchAlgorithmException | InvalidKeyException
            | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException err) {
            throw new SecurityException(ERROR_DECRYPT, err);
        }
    }

    public static void main(String[] args) {
        String priKey = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDNlgW9wcd+9izO33J2RNiviHrpIkJCXIBCA2gimGQ9X0g9PGXX3m1oV+Ninc0bgCvzp5imm1x1TaDckjAZ6Da9a0IZldJayye+E2967nh2GcXa5pTlk67pBtOzPw0Hmj0jnCCcwFdsOfxeMkrgAhE4PBz1nzA3IaI78fZdoY6VExQAa+WspYJr7HstKv8DTcDjsO4rPGMV8Sys3gLQ6XuY45zqstn3fz0srC3utUnWsbG2ZtOYykS5x86FyRbFVdkEVSib8P6cJjUMGLBULLm8uKX7o78oDxtc9lk3ja/7uXvPoSCOKuIQkxfbZRx3JmhmCELguGLUAXa4Zk03I9yVAgMBAAECggEBALf9xSOfssh4z39R0WHxma/3SHEvnPdBS/RIkkEr+8JFbSSI0mjRAz+4MKPIsJElM7C71M1/C89b61ScbPoyrhL2VFELmhhx6x6czE/L7vlbdYzEgou4kXiyCHNKQYIFUyR0jWdcaKFOgANZpVM35mtauVxF3OPqcQSPNzty5GWa8zYbN4ycIQpUdArCHPOX1/p1IO/CLivweyM6kfHkdO09GK7JCl1xgq8CVnq8eXWP/I8QYLv8AFovgNCduSTamJ5RXJ1uTYeSsTg3/YJe1J27ZuPcgB0Ll+SoF5HH4TktQhqJkA4Hio8QygOon3deulFLzXCclUsoDn0TrnABOR0CgYEA8ikR+Xw6AQVqmHq5GBIntDBVsva4BV6aNbJxmgZ3RyISGZnzlkvXVkPwGoomcHvbOvtEIJNwKHOgK4HZ6l80ibXsGINeZQlXmN9g3yMzNrNITlr0oONpm0/LdPvU+RyQLYKUXkvRuFoeToiC1fpivuEGFNYf9nhK8ERDCCAcK7cCgYEA2VXZokUnSHmAC+BqyfvaBgnxH/UFApp/5WlovgWQRzdMrNHFl6lr9RDXZFka+JRTtzkPeDJntHJlRSGTnxu1pROTMNhKV95XPffowB6cb4maOQjgFvoPacOxavq62l6D9V8/XmbtBHHVZuuzYYbDTALZg/mpEyiyiHlSfkFUUhMCgYEAjP6Ytpg79Yg/zlP2HSvE7kcOPssjzgyM4SLqvfFx1Xri6wEWMLYrxNB9phY111xmAut1eTofHY5k/a/3P1z7bAr+Ui968H0GAb5d2s3V7c1pFiYjNOH0BTfqkExobjrRP4498MFYdGwUJUmaVBgbEmRWdB9QMONNTqOQG9UGUfUCgYAnOJH88cPkFWEhQ4+iAdxXqKWmIb6UuLMKAJrRaAmggH9K1NR2yTFdWXu1dUsjvwjYkOiUxWvnqZlS/3s3hHWkY8L1VqMb9lkCKuILAbszeb6mOk7OdrQfXxx+dN8Rl2ry9gxmieaOHcjoOPL3T62g4wbEBWMYvQhzDonvlPSVlwKBgEwd8hgXw+u9pUaPxkIwfgeEUCeYeGDpAR646DhJrKDQrLTI5Gf6lkiEb8PDyt6fT8cqf1daDGel0vV6IHrG88HO0a4Kgog723Oj3KJZfqFlnT55zKIZg4JfItyopfui362Cft1F/2nf2bsdtiZJHqciZBObTvTbKsXfTQpVZpF3";

        PrivateKey privateKey = SecurityUtils.from(SecurityUtils.X509, priKey, "");
        String v = "ORC1NkvUW+FOvfdBaYy4Bh4U72NbmajyWrSbZ0sk32i1bZihdmhQ9dI3uJryR4OQpMKpsqoFimhJ0/uNsTHsNyQWMX3eq2AEL72EcnN9+F6XNRUmL/8euSl0UEL6Elk1Se7KS4vbFne93TmdFKhsmL6Y1HyPzfrWwT7rYzXYvdeYbuTW3R/D0Dtpna1Lzk3HUg4hxq1DjJu1Xj+W3dWRzemST1KlyCqAhmEiMzpmsawHq7NF4LYCZSSCe4y70F7lfMgYbAw9Rsvdax8UWcwGVk/HYozgGasS371wooDquetHB5zQreDRD9rHXyvy8YDT9syKh9WcPy94fJ6cy9daLQ==";
        String s = new String(SecurityUtils.decrypt(
            SecurityUtils.RSA_PKCS1, privateKey, org.bouncycastle.util.encoders.Base64.decode(v)));
        System.out.println(s);
    }

    /**
     * Creates a {@code PublicKey} from the base64 encoded {@code Certificate} content.
     *
     * @param type    the type of {@code PublicKey}
     * @param content the base64 encoded {@code PublicKey}
     * @return the {@code PublicKey}
     * @throws IllegalArgumentException if the content of certificate is not base64 encoded
     */
    public static PublicKey from(String type, String content) {
        Preconditions.checkNotNull(type);
        Preconditions.checkNotNull(content);
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        if (!Base64.isBase64(content)) {
            throw new IllegalArgumentException("The content of cert must be encoded as base64");
        }

        try {
            if (!X509.equals(type)) {
                return CertificateFactory
                    .getInstance(type, DEFAULT_PROVIDER)
                    .generateCertificate(new ByteArrayInputStream(Base64.decodeBase64(content)))
                    .getPublicKey();
            } else {
                return KeyFactory
                    .getInstance(type, DEFAULT_PROVIDER)
                    .generatePublic(new X509EncodedKeySpec(Base64.decodeBase64(content)));
            }
        } catch (NoSuchProviderException | NoSuchAlgorithmException
            | CertificateException | InvalidKeySpecException err) {
            throw new BaseException("Error generating the public key", err);
        }
    }

    /**
     * Use BC Provider as default provider for getting private key.
     */
    public static PrivateKey from(String type, String content, String pwd) {
        return from(type, content, pwd, DEFAULT_PROVIDER);
    }

    /**
     * Creates a {@code PrivateKey} from the base64 encoded {@code KeyStore} content.
     *
     * @param type     the type of {@code PrivateKey}
     * @param content  the base64 encoded {@code PrivateKey}
     * @param pwd      the password of {@code PrivateKey}
     * @param provider optional parameters, the name for {@link java.security.Provider} If it is
     *                 empty, the method will use system default provider.
     * @return the {@code PrivateKey}
     * @throws IllegalArgumentException if the content of key is not base64 encoded
     * @throws BaseException            if the key is failed to generate
     */
    public static PrivateKey from(String type, String content,
                                  String pwd, String provider) {
        Preconditions.checkNotNull(type);
        Preconditions.checkNotNull(content);
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        if (!Base64.isBase64(content)) {
            throw new IllegalArgumentException("The content of private key must be encoded as base64");
        }

        try {
            if (Strings.isNullOrEmpty(pwd)) {
                KeyFactory keyFactory = Strings.isNullOrEmpty(provider)
                    ? KeyFactory.getInstance(type) : KeyFactory.getInstance(type, provider);
                return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(Base64.decodeBase64(content)));
            } else {
                char[] chars = pwd.toCharArray();
                KeyStore keyStore = getKeyStore(type, content, pwd, provider);
                Enumeration<String> keyAliases = keyStore.aliases();
                String keyAlias;
                PrivateKey privateKey = null;

                while (keyAliases.hasMoreElements()) {
                    keyAlias = keyAliases.nextElement();
                    if (keyStore.isKeyEntry(keyAlias)) {
                        privateKey = (PrivateKey) keyStore.getKey(keyAlias, chars);
                        break;
                    }
                }

                if (privateKey == null) {
                    throw new InvalidKeyException("No available private key was found");
                } else {
                    return privateKey;
                }
            }
        } catch (NoSuchProviderException | NoSuchAlgorithmException | InvalidKeyException
            | InvalidKeySpecException | UnrecoverableKeyException | KeyStoreException
            | SecurityException err) {
            throw new BaseException("Error generating the private key", err);
        }
    }

    private static KeyStore getKeyStore(String type, String key, String pwd, String provider) {
        try {
            char[] chars = pwd.toCharArray();
            KeyStore keyStore = Strings.isNullOrEmpty(provider)
                ? KeyStore.getInstance(type) : KeyStore.getInstance(type, provider);
            keyStore.load(new ByteArrayInputStream(Base64.decodeBase64(key)), chars);
            return keyStore;
        } catch (NoSuchProviderException | NoSuchAlgorithmException | KeyStoreException
            | CertificateException | IOException err) {
            throw new SecurityException("Error generating the private key", err);
        }
    }

    public static String md5(String origin) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(origin.getBytes("UTF-8"));
            BigInteger bi = new BigInteger(1, md.digest());

            return bi.toString(16);
        } catch (Exception e) {
            return "";
        }
    }


    public static String getIpAddr(HttpServletRequest request) {
        String ipAddress = null;
        try {
            ipAddress = request.getHeader("x-forwarded-for");
            if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader("Proxy-Client-IP");
            }
            if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader("WL-Proxy-Client-IP");
            }
            if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getRemoteAddr();
                if (ipAddress.equals("127.0.0.1")) {
                    // 根据网卡取本机配置的IP
                    InetAddress inet = null;
                    try {
                        inet = InetAddress.getLocalHost();
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    }
                    ipAddress = inet.getHostAddress();
                }
            }
            // 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
            if (ipAddress != null && ipAddress.length() > 15) { // "***.***.***.***".length()
                // = 15
                if (ipAddress.indexOf(",") > 0) {
                    ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
                }
            }
        } catch (Exception e) {
            ipAddress="";
        }
        // ipAddress = this.getRequest().getRemoteAddr();

        return ipAddress;
    }
}
