package com.masking.aciton;

import com.masking.action.*;
import com.masking.audit.ConsoleAuditEventHandler;
import com.masking.pipeline.MaskPipeline;
import com.masking.pipeline.MaskPipelineBuilder;
import com.masking.strategy.mask.RegexMaskStrategy;
import com.masking.strategy.tokenize.UUIDTokenizationStrategy;
import com.masking.util.CryptoUtil;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

class MainIntegrationTest {
    @Test
    void fullPipeline_shouldMaskTokenizeEncryptAndAudit() {
        // Prepare record
        Map<String,String> record = new HashMap<>();
        record.put("email", "dahee@example.com");
        record.put("username", "maskingUser");
        record.put("ssn", "123456789");

        // Prepare AES key
        String keyBase64 = CryptoUtil.generateAesKeyBase64();
        byte[] aesKey = CryptoUtil.decodeBase64(keyBase64);

        // Build pipeline
        MaskPipeline pipeline = MaskPipelineBuilder.newBuilder()
                .audit("email", new ConsoleAuditEventHandler())
                .mask("email", RegexMaskStrategy.of("(?<=.).(?=[^@]+@)", '*'))
                .tokenize("username", UUIDTokenizationStrategy.of())
                .encryptAes("ssn", aesKey)
                .build();

        // Execute
        pipeline.apply(record);

        // Verify transformations
        assertEquals("d***e@example.com", record.get("email"));
        assertTrue(record.get("username").matches("[0-9a-f\\-]{36}"));
        // ssn becomes Base64 of 16-byte AES block or similar
        assertNotNull(record.get("ssn"));
        assertDoesNotThrow(() -> Base64.getDecoder().decode(record.get("ssn")));
    }
}

