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
            Cipher cipher = Cipher.getInstance(ALGORITHM);     // Cipher 객체 획득
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);      // 암호화 모드, 공개키 설정
            byte[] encrypted = cipher.doFinal(input.getBytes("UTF-8")); // 암호화 수행
            return Base64.getEncoder().encodeToString(encrypted);        // Base64로 결과 인코딩
        } catch (Exception e) {
            throw new RuntimeException("RSA 암호화 실패", e);
        }
    }
}
