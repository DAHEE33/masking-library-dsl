package com.masking.strategy.encrypt;

import com.masking.strategy.encrypt.EncryptionStrategy;
import javax.crypto.Cipher;
import java.security.PublicKey;
import java.util.Base64;

public class RsaEncryptionStrategy implements EncryptionStrategy {
    private static final String ALGORITHM = "RSA/ECB/PKCS1Padding";
    private final PublicKey publicKey;

    private RsaEncryptionStrategy(PublicKey publicKey) {
        this.publicKey = publicKey;
    }

    public static RsaEncryptionStrategy of(PublicKey publicKey) {
        return new RsaEncryptionStrategy(publicKey);
    }

    @Override
    public String encrypt(String input) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] encrypted = cipher.doFinal(input.getBytes("UTF-8"));
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new RuntimeException("RSA 암호화 실패", e);
        }
    }
}
