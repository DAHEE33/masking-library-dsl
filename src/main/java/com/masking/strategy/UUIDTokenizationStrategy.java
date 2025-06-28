package com.masking.strategy;

import java.util.UUID;

public class UUIDTokenizationStrategy implements TokenizationStrategy {

    private UUIDTokenizationStrategy() {}

    public static UUIDTokenizationStrategy create() {
        return new UUIDTokenizationStrategy();
    }

    @Override
    public String tokenize(String input) {
        // 입력 무관하게 새 UUID 생성
        return UUID.randomUUID().toString();
    }
}
