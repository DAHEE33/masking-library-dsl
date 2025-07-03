package com.masking.aciton;

import com.masking.action.*;
import com.masking.audit.ConsoleAuditEventHandler;
import com.masking.pipeline.MaskPipeline;
import com.masking.pipeline.MaskPipelineBuilder;
import com.masking.strategy.mask.RegexMaskStrategy;
import com.masking.strategy.tokenize.UUIDTokenizationStrategy;
import com.masking.util.CryptoUtil;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

class MainIntegrationTest {
    @Test
    void fullPipeline_shouldMaskTokenizeEncryptAndAudit() throws IOException {
        Map<String,String> record = new HashMap<>();
        record.put("email", "dahee@example.com");
        record.put("username", "maskingUser");
        record.put("ssn", "123456789");

        String keyBase64 = CryptoUtil.generateAesKeyBase64();
        byte[] aesKey = CryptoUtil.decodeBase64(keyBase64);

        MaskPipeline pipeline = MaskPipelineBuilder.newBuilder()
                .audit("email", new ConsoleAuditEventHandler())
                .mask("email", RegexMaskStrategy.of("(?<=.).(?=[^@]+@)", '*'))
                .tokenize("username", UUIDTokenizationStrategy.of())
                .encryptAes("ssn", aesKey)
                .build();

        pipeline.apply(record);

        assertEquals("d***e@example.com", record.get("email"));
        assertTrue(record.get("username").matches("[0-9a-f\\-]{36}"));
        //SSN 암호화 결과가 Base64 형식으로 올바르게 인코딩되었는지, 디코더가 에러를 내진 않는지
        assertNotNull(record.get("ssn"));
        assertDoesNotThrow(() -> Base64.getDecoder().decode(record.get("ssn")));
    }
}

