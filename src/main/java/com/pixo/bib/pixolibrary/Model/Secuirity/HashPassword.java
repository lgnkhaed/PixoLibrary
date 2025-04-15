package com.pixo.bib.pixolibrary.Model.Secuirity;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashPassword {
    // method to hashPassword into byte[] to be used as Seed in encryptImage and decryptImage
    public static byte[] sha256(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return digest.digest(password.getBytes());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 is not supported", e);
        }
    }
}
