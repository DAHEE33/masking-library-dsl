package com.masking.strategy.encrypt;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class AesEncryptionStrategy implements EncryptionStrategy {
    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";

    private final SecretKeySpec keySpec;
    private final IvParameterSpec ivSpec;

    private AesEncryptionStrategy(byte[] key, byte[] iv) {
        this.keySpec = new SecretKeySpec(key, "AES");
        this.ivSpec  = new IvParameterSpec(iv);
    }

    public static AesEncryptionStrategy of(String base64Key, String base64Iv) {
        byte[] key = Base64.getDecoder().decode(base64Key);
        byte[] iv  = Base64.getDecoder().decode(base64Iv);
        return new AesEncryptionStrategy(key, iv);
    }

    @Override
    public byte[] encrypt(byte[] plain) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
        return cipher.doFinal(plain);
    }

    @Override
    public byte[] decrypt(byte[] cipherText) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
        return cipher.doFinal(cipherText);
    }
}
