package com.weiyan.atp.app.controller;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;

/**
 * @author : dongliangCai
 * @since : 2022/11/4
 */

public class SHA256hash {

    public static String toHexString(byte[] hash) {
        // Convert byte array into signum representation
        BigInteger number = new BigInteger(1, hash);

        // Convert message digest into hex value
        StringBuilder hexString = new StringBuilder(number.toString(16));

        // Pad with leading zeros
        while (hexString.length() < 32) {
            hexString.insert(0, '0');
        }

        return hexString.toString();
    }

    public static String getSHA(String input) throws Exception {
        // Static getInstance method is called with hashing SHA
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long currentTime = System.currentTimeMillis();
        String stringTime = format.format(currentTime);
        // digest() method called
        // to calculate message digest of an input
        // and return array of byte
        return stringTime + " " +toHexString(md.digest(input.getBytes(StandardCharsets.UTF_8)));
    }
}