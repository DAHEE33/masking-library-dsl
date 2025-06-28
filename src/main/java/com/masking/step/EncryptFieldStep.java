package com.masking.step;

import com.masking.strategy.EncryptionStrategy;
import java.util.Base64;
import java.util.Map;

/**
 * 지정한 필드를 AES/RSA 등의 EncryptionStrategy로 암호화
 */
public class EncryptFieldStep implements Step {
    private final String fieldName;
    private final EncryptionStrategy strategy;

    private EncryptFieldStep(String fieldName, EncryptionStrategy strategy) {
        this.fieldName = fieldName;
        this.strategy  = strategy;
    }

    public static EncryptFieldStep of(String fieldName, EncryptionStrategy strategy) {
        return new EncryptFieldStep(fieldName, strategy);
    }

    @Override
    public void process(Map<String, Object> record) {
        Object value = record.get(fieldName);
        if (!(value instanceof String)) return;
        try {
            byte[] plain = ((String) value).getBytes("UTF-8");
            byte[] cipher = strategy.encrypt(plain);
            // Base64 문자열로 저장
            record.put(fieldName, Base64.getEncoder().encodeToString(cipher));
        } catch (Exception e) {
            // 암호화 실패 시 기존 값을 그대로 두거나, 예외를 기록하도록 선택
            throw new RuntimeException("EncryptFieldStep failed for field " + fieldName, e);
        }
    }
}
