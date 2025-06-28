package com.masking.step;

import com.masking.strategy.AesEncryptionStrategy;
import org.junit.jupiter.api.Test;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class EncryptFieldStepTest {

    // 테스트용 16바이트 키/IV를 Base64로 미리 인코딩
    private static final String BASE64_KEY = Base64.getEncoder()
            .encodeToString("0123456789abcdef".getBytes());
    private static final String BASE64_IV  = BASE64_KEY; // 단순 테스트용

    @Test
    void aesEncryptAndDecryptRoundTrip() throws Exception {
        String original = "sensitive-data";
        var strategy = AesEncryptionStrategy.of(BASE64_KEY, BASE64_IV);
        // 직접 암호화/복호화 검사
        byte[] cipher = strategy.encrypt(original.getBytes("UTF-8"));
        byte[] plain  = strategy.decrypt(cipher);
        assertEquals(original, new String(plain, "UTF-8"));
    }

    @Test
    void stepProcessReplacesWithBase64Cipher() {
        var strategy = AesEncryptionStrategy.of(BASE64_KEY, BASE64_IV);
        Step step = EncryptFieldStep.of("secret", strategy);
        Map<String,Object> record = new HashMap<>();
        record.put("secret", "my-password");

        step.process(record);
        String cipherText = (String) record.get("secret");

        System.out.println("암호화된 값: " + cipherText);
        assertNotNull(cipherText);

        // 복호화해서 원본 검증
        try {
            byte[] decoded = Base64.getDecoder().decode(cipherText);
            byte[] plain   = strategy.decrypt(decoded);
            assertEquals("my-password", new String(plain, "UTF-8"));
        } catch (Exception e) {
            fail("Decryption failed", e);
        }
    }
}
