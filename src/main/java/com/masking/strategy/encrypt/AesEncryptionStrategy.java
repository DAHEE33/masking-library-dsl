package com.masking.strategy.encrypt;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class AesEncryptionStrategy implements EncryptionStrategy {
    //비밀키를 만드는 데 사용
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
            //cipher 암호화, 복호화 도와줌
            Cipher cipher = Cipher.getInstance(ALGORITHM); // Cipher 객체 생성
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);    // 암호화 모드 및 키 초기화
            byte[] encrypted = cipher.doFinal(input.getBytes("UTF-8")); // 암호화 수행
            return Base64.getEncoder().encodeToString(encrypted);        // Base64 인코딩
        } catch (Exception e) {
            throw new RuntimeException("AES encryption failed", e);
        }
    }
}
