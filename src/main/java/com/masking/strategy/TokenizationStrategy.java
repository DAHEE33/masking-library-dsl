package com.masking.strategy;

/**
 * 값 → 토큰 문자열로 변환하는 전략 패턴 인터페이스
 */
public interface TokenizationStrategy {
    /**
     * @param input 원본 문자열
     * @return 치환된 토큰 문자열
     */
    String tokenize(String input);
}
