package com.masking.action;

import com.masking.strategy.TokenizationStrategy;

import java.util.Map;

public class TokenizeAction {
    private final String field;
    private final TokenizationStrategy strategy;

    private TokenizeAction(String field, TokenizationStrategy strategy) {
        this.field = field;
        this.strategy = strategy;
    }

    public static TokenizeAction of(String field, TokenizationStrategy strategy) {
        return new TokenizeAction(field, strategy);
    }

    @Override
    public void apply(Map<String, String> record) {
        String value = record.get(field);
        if (value != null) {
            record.put(field, strategy.tokenize(value));
        }
    }

}
