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

    /**
     * EncryptAction 인스턴스를 생성합니다.
     * @param field 암호화할 필드명
     * @param strategy 암호화 전략
     * @return EncryptAction 인스턴스
     */
    public static EncryptAction of(String field, EncryptionStrategy strategy) {
        return new EncryptAction(field, strategy);
    }

    /**
     * 레코드에 암호화를 적용합니다.
     * @param record 처리할 레코드
     */
    @Override
    public void apply(Map<String, String> record) {
        String value = record.get(field);
        if (value != null) {
            record.put(field, strategy.encrypt(value));
        }
    }
    
    /**
     * 암호화 대상 필드명을 반환합니다.
     * @return 필드명
     */
    public String getField() {
        return field;
    }
    
    /**
     * 암호화 전략을 반환합니다.
     * @return 암호화 전략
     */
    public EncryptionStrategy getStrategy() {
        return strategy;
    }
}
