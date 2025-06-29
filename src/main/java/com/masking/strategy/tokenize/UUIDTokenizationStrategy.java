package com.masking.strategy.tokenize;

import java.util.UUID;

/**
 * 고유 식별자(로그 추적용 등)가 필요할 때
 */
public class UUIDTokenizationStrategy implements TokenizationStrategy {

    private UUIDTokenizationStrategy() {}

    public static UUIDTokenizationStrategy of() {
        return new UUIDTokenizationStrategy();
    }

    @Override
    public String tokenize(String input) {
        return UUID.randomUUID().toString();
    }
}
