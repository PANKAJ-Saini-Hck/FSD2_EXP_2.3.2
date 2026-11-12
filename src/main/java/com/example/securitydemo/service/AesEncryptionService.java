package com.example.securitydemo.service;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Service;

@Service
public class AesEncryptionService {

    private static final String AES = "AES";
    private static final String AES_MODE = "AES/GCM/NoPadding";

    private static final String SECRET_KEY =
            "12345678901234567890123456789012";

    public String encrypt(String plainText) throws Exception {

        SecretKeySpec key = new SecretKeySpec(
                SECRET_KEY.getBytes(StandardCharsets.UTF_8), AES);

        byte[] iv = new byte[12];
        new SecureRandom().nextBytes(iv);

        Cipher cipher = Cipher.getInstance(AES_MODE);

        GCMParameterSpec parameterSpec =
                new GCMParameterSpec(128, iv);

        cipher.init(Cipher.ENCRYPT_MODE, key, parameterSpec);

        byte[] encrypted = cipher.doFinal(
                plainText.getBytes(StandardCharsets.UTF_8));

        byte[] combined = new byte[iv.length + encrypted.length];

        System.arraycopy(iv, 0, combined, 0, iv.length);
        System.arraycopy(encrypted, 0, combined, iv.length,
                encrypted.length);

        return Base64.getEncoder().encodeToString(combined);
    }

    public String decrypt(String encryptedText) throws Exception {

        byte[] combined =
                Base64.getDecoder().decode(encryptedText);

        byte[] iv = new byte[12];

        System.arraycopy(combined, 0, iv, 0, iv.length);

        byte[] encrypted =
                new byte[combined.length - iv.length];

        System.arraycopy(combined, iv.length, encrypted, 0,
                encrypted.length);

        SecretKeySpec key = new SecretKeySpec(
                SECRET_KEY.getBytes(StandardCharsets.UTF_8), AES);

        Cipher cipher = Cipher.getInstance(AES_MODE);

        GCMParameterSpec parameterSpec =
                new GCMParameterSpec(128, iv);

        cipher.init(Cipher.DECRYPT_MODE, key, parameterSpec);

        byte[] decrypted = cipher.doFinal(encrypted);

        return new String(decrypted, StandardCharsets.UTF_8);
    }
}