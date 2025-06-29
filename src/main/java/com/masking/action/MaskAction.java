package com.masking.action;

import com.masking.strategy.MaskStrategy;

import java.util.Map;

/**
 * 	특정 필드에 단일 마스킹 전략 적용
 */
public class MaskAction implements Action{

    private final String field;
    private final MaskStrategy strategy;

    private MaskAction(String field, MaskStrategy strategy) {
        this.field = field;
        this.strategy = strategy;
    }

    public static MaskAction of(String field, MaskStrategy strategy) {
        return new MaskAction(field, strategy);
    }

    @Override
    public void apply(Map<String, String> record) {
        String value = record.get(field);
        if (value != null) {
            record.put(field, strategy.mask(value));
        }
    }
}
