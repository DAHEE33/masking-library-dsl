package com.masking.aciton;

import com.masking.action.EncryptAction;
import com.masking.strategy.encrypt.AesEncryptionStrategy;
import org.junit.jupiter.api.Test;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class EncryptActionTest {

    @Test
    void aesEncrypt_shouldProduceBase64() {
        byte[] key = "1234567812345678".getBytes(); // 16-byte AES key
        EncryptAction action = EncryptAction.of("field", AesEncryptionStrategy.of(key));
        Map<String, String> record = new HashMap<>();
        record.put("field", "test");

        action.apply(record);
        String encrypted = record.get("field");
        assertNotNull(encrypted);
        assertTrue(Base64.getDecoder().decode(encrypted).length > 0);
    }
}

