package com.masking.util;


import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.*;
import java.util.Base64;

/**
 *  암호화를 위한 메서드
 */
public class CryptoUtil {

    /**
     * AES 128-bit 키를 생성해 Base64 인코딩 문자열로 반환
     * @return Base64로 인코딩된 AES 128비트 키 문자열
     */
    public static String generateAesKeyBase64() {
        try {
            KeyGenerator kg = KeyGenerator.getInstance("AES"); // 키 생성기 인스턴스 획득
            kg.init(128);                                  // 128비트 키 크기 설정
            SecretKey key = kg.generateKey();              // 키 생성
            return Base64.getEncoder().encodeToString(key.getEncoded()); // Base64 인코딩
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("AES 키 생성 실패", e);
        }
    }

    /**
     * Base64 문자열을 디코딩해 바이트 배열로 반환
     * @param base64 디코딩할 Base64 문자열
     * @return 디코딩된 바이트 배열
     */
    public static byte[] decodeBase64(String base64) {
        return Base64.getDecoder().decode(base64);
    }

    /**
     * 바이트 배열을 Base64 문자열로 인코딩해 반환
     * @param data 인코딩할 바이트 배열
     * @return Base64로 인코딩된 문자열
     */
    public static String encodeBase64(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }

    /**
     * RSA 키 페어 생성 (기본 2048비트)
     * @return 생성된 RSA KeyPair
     */
    public static KeyPair generateRsaKeyPair() {
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(2048);
            return kpg.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("RSA 키 페어 생성 실패", e);
        }
    }
}
