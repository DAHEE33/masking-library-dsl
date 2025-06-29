package com.masking.strategy;

import java.util.Random;

public class NumericTokenizationStrategy implements TokenizationStrategy {
    private final int length;
    private final Random random = new Random();

    private NumericTokenizationStrategy(int length) {
        this.length = length;
    }

    public static NumericTokenizationStrategy of(int length) {
        return new NumericTokenizationStrategy(length);
    }

    @Override
    public String tokenize(String input) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
}
