package com.cts.trialledger.util;

import java.io.File;

import java.io.FileInputStream;

import java.security.MessageDigest;

public class HashUtil {

    public static String generateSHA256(String filePath) throws Exception {

        File file = new File(filePath);

        MessageDigest digest = MessageDigest.getInstance("SHA-256");

        FileInputStream fis = new FileInputStream(file);

        byte[] byteArray = new byte[1024];

        int bytesCount;

        while ((bytesCount = fis.read(byteArray)) != -1) {

            digest.update(byteArray, 0, bytesCount);

        }

        fis.close();

        byte[] bytes = digest.digest();

        StringBuilder hexString = new StringBuilder();

        for (byte b : bytes) {

            hexString.append(String.format("%02x", b));

        }

        return hexString.toString();

    }


}