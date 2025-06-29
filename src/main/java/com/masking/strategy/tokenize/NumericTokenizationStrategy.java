package com.masking.strategy.tokenize;

import java.util.Random;

/**
 * 짧고 숫자만으로 된 임시 ID나 코드(예: 6자리 인증번호) 용도로 쓸 때
 */
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
