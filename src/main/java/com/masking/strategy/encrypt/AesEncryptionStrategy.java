package com.masking.strategy.encrypt;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class AesEncryptionStrategy implements EncryptionStrategy {
    private final SecretKeySpec keySpec;
    private static final String ALGORITHM = "AES/ECB/PKCS5Padding";

    private AesEncryptionStrategy(byte[] key) {
        this.keySpec = new SecretKeySpec(key, "AES");
    }

    public static AesEncryptionStrategy of(byte[] key) {
        return new AesEncryptionStrategy(key);
    }

    @Override
    public String encrypt(String input) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            byte[] encrypted = cipher.doFinal(input.getBytes("UTF-8"));
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new RuntimeException("AES encryption failed", e);
        }
    }
}
