package com.masking.step;

import com.masking.strategy.tokenize.TokenizationStrategy;

import java.util.Map;

/**
 * 지정한 필드의 원본 값을 TokenizationStrategy로 치환
 */
public class TokenizeFieldStep implements Step {
    private final String fieldName;
    private final TokenizationStrategy strategy;

    private TokenizeFieldStep(String fieldName, TokenizationStrategy strategy) {
        this.fieldName = fieldName;
        this.strategy = strategy;
    }

    public static TokenizeFieldStep of(String fieldName, TokenizationStrategy strategy) {
        return new TokenizeFieldStep(fieldName, strategy);
    }

    @Override
    public void process(Map<String, Object> record) {
        Object value = record.get(fieldName);
        if (value == null) return;
        // 전략에 따라 문자열로 변환한 토큰으로 대체
        String token = strategy.tokenize(value.toString());
        record.put(fieldName, token);
    }
}
