package com.masking.strategy.encrypt;

/**
 * 바이트 배열 입력을 암호화된 바이트 배열로 반환하는 전략 패턴 인터페이스
 */
public interface EncryptionStrategy {
    String encrypt(String input);
}
