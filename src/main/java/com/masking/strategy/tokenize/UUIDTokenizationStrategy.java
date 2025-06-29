package com.masking.strategy.tokenize;

import java.util.UUID;

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
