package com.masking.action;

import com.masking.strategy.encrypt.EncryptionStrategy;

import java.util.Map;

public class EncryptAction implements Action {
    private final String field;
    private final EncryptionStrategy strategy;

    private EncryptAction(String field, EncryptionStrategy strategy) {
        this.field = field;
        this.strategy = strategy;
    }

    public static EncryptAction of(String field, EncryptionStrategy strategy) {
        return new EncryptAction(field, strategy);
    }

    @Override
    public void apply(Map<String, String> record) {
        String value = record.get(field);
        if (value != null) {
            record.put(field, strategy.encrypt(value));
        }
    }
}
