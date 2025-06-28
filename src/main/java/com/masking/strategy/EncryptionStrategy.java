package com.masking.strategy;

/**
 * 바이트 배열 입력을 암호화된 바이트 배열로 반환하는 전략 패턴 인터페이스
 */
public interface EncryptionStrategy {
    /**
     * @param plain 입력 바이트 배열 (UTF-8 String.getBytes())
     * @return 암호화된 바이트 배열
     * @throws Exception 암호화 실패 시
     */
    byte[] encrypt(byte[] plain) throws Exception;

    /**
     * @param cipher 암호화된 바이트 배열
     * @return 복호화된 바이트 배열
     * @throws Exception 복호화 실패 시
     */
    byte[] decrypt(byte[] cipher) throws Exception;
}
